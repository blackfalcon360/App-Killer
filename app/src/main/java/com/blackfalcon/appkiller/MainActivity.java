package com.blackfalcon.appkiller;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends Activity {

    static class AppItem {
        String pkg;
        String label;
        boolean system;
        boolean stopped;
        boolean frozen;
        Drawable icon;
    }

    private static final int MATCH = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP = ViewGroup.LayoutParams.WRAP_CONTENT;

    private int bg, card, tx, sub;
    private int tab = 0;
    private PackageManager pm;
    private final HashSet<String> selected = new HashSet<String>();
    private ArrayList<AppItem> all = new ArrayList<AppItem>();
    private ArrayList<AppItem> shown = new ArrayList<AppItem>();
    private boolean showSystem = false;
    private String query = "";

    private FrameLayout content;
    private Button tabB0, tabB1;
    private TextView accTv, rootTv, countTv;
    private Button cancelBtn;
    private AppAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pm = getPackageManager();
        build();
        loadApps();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Bridge.resume(this);
        refreshStatus();
        if (Bridge.dirty) {
            Bridge.dirty = false;
            loadApps();
        }
    }

    @Override
    public void onBackPressed() {
        dlg().setTitle(t("exit_title")).setMessage(t("exit_msg"))
                .setPositiveButton(t("yes"), (d, w) -> finish())
                .setNegativeButton(t("no"), null)
                .show();
    }

    // ---------- helpers ----------

    private String t(String k) { return S.t(this, k); }

    private void toast(String s) { Toast.makeText(this, s, Toast.LENGTH_LONG).show(); }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density + 0.5f); }

    private GradientDrawable rounded(int color, int r) {
        GradientDrawable g = new GradientDrawable();
        g.setColor(color);
        g.setCornerRadius(dp(r));
        return g;
    }

    private AlertDialog.Builder dlg() {
        return new AlertDialog.Builder(this, Store.dark(this)
                ? android.R.style.Theme_DeviceDefault_Dialog_Alert
                : android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
    }

    private TextView tv(String s, int sp, int color) {
        TextView v = new TextView(this);
        v.setText(s);
        v.setTextSize(sp);
        v.setTextColor(color);
        return v;
    }

    private Button mk(String text, int color, View.OnClickListener l) {
        Button b = new Button(this);
        b.setText(text);
        b.setAllCaps(false);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setBackground(rounded(color, 10));
        b.setPadding(dp(6), dp(6), dp(6), dp(6));
        b.setOnClickListener(l);
        return b;
    }

    private LinearLayout.LayoutParams weighted() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(0, WRAP, 1f);
        p.setMargins(dp(3), dp(3), dp(3), dp(3));
        return p;
    }

    /** Horizontal row, already added to parent with MATCH_PARENT width (children can use weight). */
    private LinearLayout row(LinearLayout parent) {
        LinearLayout r = new LinearLayout(this);
        r.setOrientation(LinearLayout.HORIZONTAL);
        r.setGravity(Gravity.CENTER_VERTICAL);
        parent.addView(r, new LinearLayout.LayoutParams(MATCH, WRAP));
        return r;
    }

    private LinearLayout cardBox() {
        LinearLayout v = new LinearLayout(this);
        v.setOrientation(LinearLayout.VERTICAL);
        v.setPadding(dp(10), dp(10), dp(10), dp(10));
        v.setBackground(rounded(card, 12));
        return v;
    }

    private void tint(CompoundButton b) {
        b.setTextColor(tx);
        b.setButtonTintList(ColorStateList.valueOf(tx));
    }

    private LinearLayout.LayoutParams gap() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(MATCH, WRAP);
        p.setMargins(0, 0, 0, dp(8));
        return p;
    }

    // ---------- main layout ----------

    private void build() {
        boolean dark = Store.dark(this);
        bg = dark ? Color.BLACK : 0xFFF2F2F2;
        card = dark ? 0xFF1C1C1E : Color.WHITE;
        tx = dark ? Color.WHITE : 0xFF111111;
        sub = dark ? 0xFFAAAAAA : 0xFF555555;

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(bg);
        root.setPadding(dp(10), dp(10), dp(10), dp(6));
        root.setLayoutDirection("en".equals(Store.lang(this))
                ? View.LAYOUT_DIRECTION_LTR : View.LAYOUT_DIRECTION_RTL);

        TextView title = tv(t("app_name"), 22, tx);
        title.setGravity(Gravity.CENTER);
        title.setPadding(0, 0, 0, dp(8));
        root.addView(title, new LinearLayout.LayoutParams(MATCH, WRAP));

        content = new FrameLayout(this);
        root.addView(content, new LinearLayout.LayoutParams(MATCH, 0, 1f));

        LinearLayout tabs = row(root);
        tabB0 = mk(t("tab_apps"), 0xFF455A64, v -> { tab = 0; showTab(); });
        tabB1 = mk(t("tab_settings"), 0xFF455A64, v -> { tab = 1; showTab(); });
        tabs.addView(tabB0, weighted());
        tabs.addView(tabB1, weighted());

        TextView by = tv(t("by"), 13, sub);
        by.setGravity(Gravity.CENTER);
        by.setPadding(0, dp(4), 0, 0);
        root.addView(by, new LinearLayout.LayoutParams(MATCH, WRAP));

        setContentView(root);
        showTab();
    }

    private void showTab() {
        content.removeAllViews();
        tabB0.setBackground(rounded(tab == 0 ? 0xFFD32F2F : 0xFF455A64, 10));
        tabB1.setBackground(rounded(tab == 1 ? 0xFFD32F2F : 0xFF455A64, 10));
        View v = (tab == 0) ? buildApps() : buildSettings();
        content.addView(v, new FrameLayout.LayoutParams(MATCH, MATCH));
    }

    // ---------- Apps tab ----------

    private View buildApps() {
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);

        LinearLayout st = cardBox();
        accTv = tv("", 13, tx);
        rootTv = tv("", 13, tx);
        st.addView(accTv);
        st.addView(rootTv);
        LinearLayout r1 = row(st);
        r1.addView(mk(t("btn_acc"), 0xFF455A64, v -> openAccessibility()), weighted());
        r1.addView(mk(t("btn_root"), 0xFF455A64, v -> checkRoot()), weighted());
        cancelBtn = mk(t("btn_cancel_auto"), 0xFFC62828, v -> {
            Bridge.cancel();
            refreshStatus();
        });
        st.addView(cancelBtn, new LinearLayout.LayoutParams(MATCH, WRAP));
        box.addView(st, gap());

        EditText search = new EditText(this);
        search.setSingleLine(true);
        search.setTextColor(Color.BLACK);
        search.setHintTextColor(0xFF777777);
        search.setHint(t("search_hint"));
        search.setBackground(rounded(Color.WHITE, 10));
        search.setPadding(dp(12), dp(8), dp(12), dp(8));
        search.setText(query);
        search.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void onTextChanged(CharSequence s, int a, int b, int c) { }
            @Override public void afterTextChanged(Editable e) {
                query = e.toString().trim().toLowerCase(Locale.ROOT);
                applyFilter();
            }
        });
        box.addView(search, gap());

        LinearLayout r2 = row(box);
        CheckBox sysCb = new CheckBox(this);
        sysCb.setText(t("show_system"));
        sysCb.setChecked(showSystem);
        tint(sysCb);
        sysCb.setOnCheckedChangeListener((btn, checked) -> {
            showSystem = checked;
            applyFilter();
        });
        r2.addView(sysCb, new LinearLayout.LayoutParams(0, WRAP, 1f));
        r2.addView(mk(t("select_all"), 0xFF455A64, v -> selectAll()), new LinearLayout.LayoutParams(WRAP, WRAP));
        r2.addView(mk(t("select_none"), 0xFF455A64, v -> {
            selected.clear();
            adapter.notifyDataSetChanged();
            updateCount();
        }), new LinearLayout.LayoutParams(WRAP, WRAP));

        countTv = tv("", 12, sub);
        countTv.setPadding(dp(4), dp(2), dp(4), dp(4));
        box.addView(countTv, new LinearLayout.LayoutParams(MATCH, WRAP));

        ListView lv = new ListView(this);
        lv.setDivider(new ColorDrawable(Color.TRANSPARENT));
        lv.setDividerHeight(dp(4));
        lv.setCacheColorHint(Color.TRANSPARENT);
        adapter = new AppAdapter();
        lv.setAdapter(adapter);
        lv.setOnItemClickListener((parent, view, pos, id) -> {
            if (pos < 0 || pos >= shown.size()) return;
            String p = shown.get(pos).pkg;
            if (!selected.remove(p)) selected.add(p);
            adapter.notifyDataSetChanged();
            updateCount();
        });
        box.addView(lv, new LinearLayout.LayoutParams(MATCH, 0, 1f));

        LinearLayout a1 = row(box);
        a1.addView(mk(t("kill_bg"), 0xFFEF6C00, v -> doKill()), weighted());
        a1.addView(mk(t("force_stop"), 0xFFC62828, v -> doForceStop()), weighted());
        LinearLayout a2 = row(box);
        a2.addView(mk(t("freeze"), 0xFF1565C0, v -> doFreeze(true)), weighted());
        a2.addView(mk(t("unfreeze"), 0xFF2E7D32, v -> doFreeze(false)), weighted());

        refreshStatus();
        applyFilter();
        return box;
    }

    private void refreshStatus() {
        if (accTv == null || rootTv == null || cancelBtn == null) return;
        accTv.setText(ForceStopService.isOn() ? t("acc_on") : t("acc_off"));
        rootTv.setText(Store.rootOk(this) ? t("root_yes") : t("root_no"));
        cancelBtn.setVisibility(Bridge.active ? View.VISIBLE : View.GONE);
    }

    private void updateCount() {
        if (countTv == null) return;
        countTv.setText(t("count_sel") + ": " + selected.size() + " / " + shown.size());
    }

    private void applyFilter() {
        ArrayList<AppItem> out = new ArrayList<AppItem>();
        for (AppItem it : all) {
            if (it.system && !showSystem && !it.frozen) continue;
            if (!query.isEmpty()
                    && !it.label.toLowerCase(Locale.ROOT).contains(query)
                    && !it.pkg.toLowerCase(Locale.ROOT).contains(query)) continue;
            out.add(it);
        }
        shown = out;
        if (adapter != null) adapter.notifyDataSetChanged();
        updateCount();
    }

    private void selectAll() {
        Set<String> prot = protectedPkgs();
        for (AppItem it : shown) {
            if (!prot.contains(it.pkg)) selected.add(it.pkg);
        }
        if (adapter != null) adapter.notifyDataSetChanged();
        updateCount();
    }

    private void loadApps() {
        new Thread(() -> {
            ArrayList<AppItem> list = new ArrayList<AppItem>();
            try {
                List<ApplicationInfo> ais = pm.getInstalledApplications(0);
                for (ApplicationInfo ai : ais) {
                    if (ai.packageName.equals(getPackageName())) continue;
                    AppItem it = new AppItem();
                    it.pkg = ai.packageName;
                    CharSequence l = ai.loadLabel(pm);
                    it.label = (l == null) ? ai.packageName : l.toString();
                    it.system = (ai.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
                    it.stopped = (ai.flags & ApplicationInfo.FLAG_STOPPED) != 0;
                    it.frozen = !ai.enabled;
                    list.add(it);
                }
            } catch (Exception e) {
                // leave the list as it is
            }
            Collections.sort(list, new Comparator<AppItem>() {
                @Override
                public int compare(AppItem a, AppItem b) {
                    return a.label.compareToIgnoreCase(b.label);
                }
            });
            final ArrayList<AppItem> res = list;
            runOnUiThread(() -> {
                all = res;
                applyFilter();
            });
        }).start();
    }

    private class Holder {
        CheckBox cb;
        ImageView ic;
        TextView name, pkg, status;
        Button info;
    }

    private class AppAdapter extends BaseAdapter {
        @Override public int getCount() { return shown.size(); }
        @Override public Object getItem(int i) { return shown.get(i); }
        @Override public long getItemId(int i) { return i; }

        @Override
        public View getView(int pos, View cv, ViewGroup parent) {
            Holder h;
            if (cv == null) {
                LinearLayout r = new LinearLayout(MainActivity.this);
                r.setOrientation(LinearLayout.HORIZONTAL);
                r.setGravity(Gravity.CENTER_VERTICAL);
                r.setPadding(dp(8), dp(8), dp(8), dp(8));
                r.setBackground(rounded(card, 10));
                r.setLayoutParams(new AbsListView.LayoutParams(MATCH, WRAP));

                h = new Holder();
                h.cb = new CheckBox(MainActivity.this);
                h.cb.setClickable(false);
                h.cb.setFocusable(false);
                h.cb.setButtonTintList(ColorStateList.valueOf(tx));
                r.addView(h.cb, new LinearLayout.LayoutParams(WRAP, WRAP));

                h.ic = new ImageView(MainActivity.this);
                LinearLayout.LayoutParams ip = new LinearLayout.LayoutParams(dp(40), dp(40));
                ip.setMargins(dp(4), 0, dp(8), 0);
                r.addView(h.ic, ip);

                LinearLayout col = new LinearLayout(MainActivity.this);
                col.setOrientation(LinearLayout.VERTICAL);
                h.name = tv("", 15, tx);
                h.pkg = tv("", 11, sub);
                h.status = tv("", 12, sub);
                h.name.setSingleLine(true);
                h.pkg.setSingleLine(true);
                h.name.setEllipsize(TextUtils.TruncateAt.END);
                h.pkg.setEllipsize(TextUtils.TruncateAt.END);
                col.addView(h.name);
                col.addView(h.pkg);
                col.addView(h.status);
                r.addView(col, new LinearLayout.LayoutParams(0, WRAP, 1f));

                h.info = mk(t("info"), 0xFF455A64, null);
                h.info.setFocusable(false);
                r.addView(h.info, new LinearLayout.LayoutParams(WRAP, WRAP));

                r.setTag(h);
                cv = r;
            } else {
                h = (Holder) cv.getTag();
            }
            final AppItem it = shown.get(pos);
            if (it.icon == null) {
                try {
                    it.icon = pm.getApplicationIcon(it.pkg);
                } catch (Exception e) {
                    // no icon
                }
            }
            h.ic.setImageDrawable(it.icon);
            h.name.setText(it.label);
            h.pkg.setText(it.pkg);
            h.cb.setChecked(selected.contains(it.pkg));
            if (it.frozen) {
                h.status.setText(t("st_frozen"));
                h.status.setTextColor(0xFF42A5F5);
            } else if (it.stopped) {
                h.status.setText(t("st_stopped"));
                h.status.setTextColor(0xFF66BB6A);
            } else {
                h.status.setText(t("st_active"));
                h.status.setTextColor(0xFFFFA726);
            }
            h.info.setOnClickListener(v -> openInfo(it.pkg));
            return cv;
        }
    }

    // ---------- actions ----------

    private void openInfo(String pkg) {
        try {
            startActivity(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + pkg)));
        } catch (Exception e) {
            toast(e.getMessage() == null ? "Error" : e.getMessage());
        }
    }

    private void openAccessibility() {
        try {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
        } catch (Exception e) {
            toast(e.getMessage() == null ? "Error" : e.getMessage());
        }
    }

    private Set<String> protectedPkgs() {
        HashSet<String> s = new HashSet<String>();
        s.add("android");
        s.add("com.android.systemui");
        s.add("com.android.settings");
        s.add("com.android.phone");
        s.add("com.android.providers.telephony");
        s.add("com.android.providers.settings");
        s.add("com.google.android.gms");
        s.add(getPackageName());
        try {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            ResolveInfo ri = pm.resolveActivity(home, PackageManager.MATCH_DEFAULT_ONLY);
            if (ri != null && ri.activityInfo != null) s.add(ri.activityInfo.packageName);
        } catch (Exception e) {
            // ignore
        }
        return s;
    }

    private ArrayList<String> pickSelected(boolean skipProtected) {
        ArrayList<String> out = new ArrayList<String>();
        if (selected.isEmpty()) {
            toast(t("none_selected"));
            return out;
        }
        Set<String> prot = protectedPkgs();
        boolean skipped = false;
        for (String p : selected) {
            if (!p.matches("[A-Za-z0-9._]+")) continue;
            if (skipProtected && prot.contains(p)) {
                skipped = true;
                continue;
            }
            out.add(p);
        }
        Collections.sort(out);
        if (skipped) toast(t("prot_skipped"));
        return out;
    }

    private void doKill() {
        ArrayList<String> list = pickSelected(false);
        if (list.isEmpty()) return;
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (String p : list) {
            try {
                am.killBackgroundProcesses(p);
            } catch (Exception e) {
                // ignore this package
            }
        }
        toast(String.format(Locale.US, t("kill_done"), list.size()));
    }

    private void doForceStop() {
        final ArrayList<String> list = pickSelected(true);
        if (list.isEmpty()) return;
        dlg().setTitle(t("confirm_title"))
                .setMessage(String.format(Locale.US, t("confirm_fs"), list.size()))
                .setPositiveButton(t("yes"), (d, w) -> startForceStop(list))
                .setNegativeButton(t("no"), null)
                .show();
    }

    private void startForceStop(final ArrayList<String> list) {
        if (Store.rootOk(this)) {
            runRoot(list, "am force-stop %s");
        } else if (ForceStopService.isOn()) {
            toast(t("auto_started"));
            Bridge.begin(this, list);
        } else {
            dlg().setTitle(t("btn_acc")).setMessage(t("acc_needed"))
                    .setPositiveButton(t("btn_acc"), (d, w) -> openAccessibility())
                    .setNegativeButton(t("close"), null)
                    .show();
        }
    }

    private void doFreeze(final boolean freeze) {
        final ArrayList<String> list = pickSelected(true);
        if (list.isEmpty()) return;
        if (Store.rootOk(this)) {
            String msg = String.format(Locale.US, t(freeze ? "confirm_fz" : "confirm_unfz"), list.size());
            dlg().setTitle(t("confirm_title")).setMessage(msg)
                    .setPositiveButton(t("yes"), (d, w) ->
                            runRoot(list, freeze ? "pm disable-user --user 0 %s" : "pm enable %s"))
                    .setNegativeButton(t("no"), null)
                    .show();
        } else {
            showAdb(list, freeze);
        }
    }

    private void showAdb(ArrayList<String> list, boolean freeze) {
        StringBuilder sb = new StringBuilder();
        for (String p : list) {
            sb.append("adb shell ")
                    .append(freeze ? "pm disable-user --user 0 " : "pm enable ")
                    .append(p).append("\n");
        }
        final String cmds = sb.toString();
        dlg().setTitle("ADB").setMessage(t("no_root_msg") + "\n\n" + cmds)
                .setPositiveButton(t("copy"), (d, w) -> {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cm.setPrimaryClip(ClipData.newPlainText("adb", cmds));
                    toast(t("copied"));
                })
                .setNegativeButton(t("close"), null)
                .show();
    }

    private void runRoot(final List<String> list, final String fmt) {
        new Thread(() -> {
            int ok = 0;
            int bad = 0;
            for (String p : list) {
                if (Root.run(String.format(Locale.US, fmt, p))) ok++;
                else bad++;
            }
            final int o = ok;
            final int b = bad;
            runOnUiThread(() -> {
                toast(String.format(Locale.US, t("root_done"), o, b));
                loadApps();
            });
        }).start();
    }

    private void checkRoot() {
        new Thread(() -> {
            final boolean ok = Root.run("id");
            Store.setRootOk(MainActivity.this, ok);
            runOnUiThread(() -> {
                refreshStatus();
                toast(t(ok ? "root_checked_ok" : "root_checked_no"));
            });
        }).start();
    }

    // ---------- Settings tab ----------

    private View buildSettings() {
        ScrollView sv = new ScrollView(this);
        LinearLayout box = new LinearLayout(this);
        box.setOrientation(LinearLayout.VERTICAL);
        sv.addView(box, new ViewGroup.LayoutParams(MATCH, WRAP));

        LinearLayout c1 = cardBox();
        c1.addView(tv(t("lang_title"), 16, tx));
        final RadioGroup rgL = new RadioGroup(this);
        String[] names = {"English", "اردو", "العربية"};
        String[] codes = {"en", "ur", "ar"};
        String cur = Store.lang(this);
        for (int i = 0; i < 3; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setId(101 + i);
            rb.setText(names[i]);
            tint(rb);
            rgL.addView(rb);
            if (codes[i].equals(cur)) rb.setChecked(true);
        }
        c1.addView(rgL);
        box.addView(c1, gap());

        LinearLayout c2 = cardBox();
        c2.addView(tv(t("theme_title"), 16, tx));
        final RadioGroup rgT = new RadioGroup(this);
        RadioButton rd = new RadioButton(this);
        rd.setId(111);
        rd.setText(t("dark"));
        tint(rd);
        RadioButton rl = new RadioButton(this);
        rl.setId(112);
        rl.setText(t("light"));
        tint(rl);
        rgT.addView(rd);
        rgT.addView(rl);
        if (Store.dark(this)) rd.setChecked(true);
        else rl.setChecked(true);
        c2.addView(rgT);
        box.addView(c2, gap());

        box.addView(mk(t("save"), 0xFFD32F2F, v -> {
            int id = rgL.getCheckedRadioButtonId();
            Store.setLang(MainActivity.this, id == 102 ? "ur" : (id == 103 ? "ar" : "en"));
            Store.setDark(MainActivity.this, rgT.getCheckedRadioButtonId() != 112);
            build();
            toast(t("saved"));
        }), gap());

        LinearLayout c3 = cardBox();
        c3.addView(tv(t("privacy_title"), 16, tx));
        TextView body = tv(t("privacy"), 13, sub);
        body.setPadding(0, dp(6), 0, dp(6));
        c3.addView(body);
        c3.addView(mk(t("btn_acc"), 0xFF455A64, v -> openAccessibility()),
                new LinearLayout.LayoutParams(MATCH, WRAP));
        box.addView(c3, gap());
        return sv;
    }
}
