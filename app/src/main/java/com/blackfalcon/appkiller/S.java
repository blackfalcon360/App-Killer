package com.blackfalcon.appkiller;

import android.content.Context;
import java.util.HashMap;

/** All UI text in English (0), Urdu (1), Arabic (2). */
final class S {
    private S() {}

    private static final HashMap<String, String[]> M = new HashMap<String, String[]>();

    private static void a(String k, String en, String ur, String ar) {
        M.put(k, new String[]{en, ur, ar});
    }

    static {
        a("app_name", "App Killer", "ایپ کلر", "قاتل التطبيقات");
        a("tab_apps", "Apps", "ایپس", "التطبيقات");
        a("tab_settings", "Settings", "سیٹنگز", "الإعدادات");
        a("by", "By: Black Falcon \uD83E\uDD85", "By: Black Falcon \uD83E\uDD85", "By: Black Falcon \uD83E\uDD85");
        a("search_hint", "Search apps...", "ایپ تلاش کریں...", "ابحث عن تطبيق...");
        a("show_system", "Show system apps", "سسٹم ایپس دکھائیں", "إظهار تطبيقات النظام");
        a("select_all", "Select all", "سب منتخب کریں", "تحديد الكل");
        a("select_none", "Clear", "صاف کریں", "مسح");
        a("kill_bg", "Kill background", "بیک گراؤنڈ بند", "إنهاء الخلفية");
        a("force_stop", "Force stop", "فورس اسٹاپ", "إيقاف إجباري");
        a("freeze", "Freeze", "فریز", "تجميد");
        a("unfreeze", "Unfreeze", "ان فریز", "إلغاء التجميد");
        a("info", "Info", "معلومات", "معلومات");
        a("st_stopped", "Stopped", "رکی ہوئی", "متوقف");
        a("st_frozen", "Frozen", "فریز", "مجمّد");
        a("st_active", "Not stopped (may be running)", "رکی نہیں (چل رہی ہو سکتی ہے)", "غير متوقف (قد يعمل)");
        a("count_sel", "Selected", "منتخب", "المحدد");
        a("none_selected", "Select at least one app first.", "پہلے کم از کم ایک ایپ منتخب کریں۔", "حدد تطبيقًا واحدًا على الأقل أولاً.");
        a("prot_skipped", "Protected system apps (launcher, Settings, System UI) were skipped.", "محفوظ سسٹم ایپس (لانچر، سیٹنگز، سسٹم UI) چھوڑ دی گئیں۔", "تم تخطي تطبيقات النظام المحمية (الشاشة الرئيسية، الإعدادات، واجهة النظام).");
        a("status_title", "Status", "اسٹیٹس", "الحالة");
        a("acc_on", "Accessibility service: ON", "ایکسیسبیلٹی سروس: آن", "خدمة إمكانية الوصول: مفعّلة");
        a("acc_off", "Accessibility service: OFF", "ایکسیسبیلٹی سروس: بند", "خدمة إمكانية الوصول: متوقفة");
        a("root_yes", "Root: available", "روٹ: دستیاب", "الروت: متاح");
        a("root_no", "Root: not available / not checked", "روٹ: دستیاب نہیں / چیک نہیں کیا", "الروت: غير متاح / لم يتم الفحص");
        a("btn_acc", "Enable accessibility", "ایکسیسبیلٹی آن کریں", "تفعيل إمكانية الوصول");
        a("btn_root", "Check root", "روٹ چیک کریں", "فحص الروت");
        a("btn_cancel_auto", "Cancel automation", "آٹومیشن منسوخ کریں", "إلغاء الأتمتة");
        a("confirm_title", "Confirm", "تصدیق", "تأكيد");
        a("confirm_fs", "Force stop %d app(s)?", "%d ایپ(س) فورس اسٹاپ کریں؟", "إيقاف %d تطبيق(ات) إجباريًا؟");
        a("confirm_fz", "Freeze %d app(s) using root? A frozen app disappears from the launcher and cannot run until unfrozen.", "روٹ سے %d ایپ(س) فریز کریں؟ فریز ایپ لانچر سے غائب ہو جاتی ہے اور ان فریز ہونے تک نہیں چل سکتی۔", "تجميد %d تطبيق(ات) باستخدام الروت؟ التطبيق المجمّد يختفي من القائمة ولا يعمل حتى يُلغى تجميده.");
        a("confirm_unfz", "Unfreeze %d app(s) using root?", "روٹ سے %d ایپ(س) ان فریز کریں؟", "إلغاء تجميد %d تطبيق(ات) باستخدام الروت؟");
        a("yes", "Yes", "ہاں", "نعم");
        a("no", "No", "نہیں", "لا");
        a("close", "Close", "بند کریں", "إغلاق");
        a("exit_title", "Exit", "باہر نکلیں", "خروج");
        a("exit_msg", "Do you want to exit the app?", "کیا آپ ایپ سے باہر نکلنا چاہتے ہیں؟", "هل تريد الخروج من التطبيق؟");
        a("kill_done", "Asked Android to stop the background processes of %d app(s). Apps with a foreground service, or apps that restart themselves, may come back.", "%d ایپ(س) کے بیک گراؤنڈ پروسیس بند کرنے کی درخواست کی گئی۔ فورگراؤنڈ سروس والی یا خود کو دوبارہ چلانے والی ایپس واپس آ سکتی ہیں۔", "تم طلب إيقاف عمليات الخلفية لـ %d تطبيق(ات). قد تعود التطبيقات التي لها خدمة في الواجهة أو التي تعيد تشغيل نفسها.");
        a("acc_needed", "Force stop without root needs the accessibility service. Turn on \"App Killer\" in Accessibility settings (on Android 13+ you may first have to open this app's App Info, tap the three-dot menu and choose \"Allow restricted settings\"), then come back and try again.", "روٹ کے بغیر فورس اسٹاپ کے لیے ایکسیسبیلٹی سروس چاہیے۔ ایکسیسبیلٹی سیٹنگز میں \"App Killer\" آن کریں (اینڈرائیڈ 13+ پر پہلے اس ایپ کی ایپ انفو میں تین نقطوں کے مینو سے \"Allow restricted settings\" منتخب کرنا پڑ سکتا ہے)، پھر واپس آ کر دوبارہ کوشش کریں۔", "الإيقاف الإجباري بدون روت يحتاج خدمة إمكانية الوصول. فعّل \"App Killer\" في إعدادات إمكانية الوصول (في أندرويد 13+ قد تحتاج أولاً لفتح معلومات هذا التطبيق ثم القائمة ثم \"السماح بالإعدادات المقيّدة\")، ثم عد وحاول مرة أخرى.");
        a("auto_started", "Automation started. Please do not touch the screen.", "آٹومیشن شروع ہو گئی۔ براہ کرم اسکرین کو ہاتھ نہ لگائیں۔", "بدأت الأتمتة. من فضلك لا تلمس الشاشة.");
        a("auto_done", "Finished. Done: %d, skipped: %d.", "مکمل۔ ہو گئیں: %d، چھوڑی گئیں: %d۔", "انتهى. تم: %d، تم تخطي: %d.");
        a("root_done", "Root finished: %d ok, %d failed.", "روٹ کا کام مکمل: %d کامیاب، %d ناکام۔", "انتهى الروت: %d نجح، %d فشل.");
        a("root_checked_ok", "Root access granted.", "روٹ رسائی مل گئی۔", "تم منح صلاحية الروت.");
        a("root_checked_no", "No root access. Freeze needs root; use the ADB commands instead.", "روٹ رسائی نہیں ملی۔ فریز کے لیے روٹ چاہیے؛ اس کی جگہ ADB کمانڈز استعمال کریں۔", "لا توجد صلاحية الروت. التجميد يحتاج روت؛ استخدم أوامر ADB بدلاً من ذلك.");
        a("no_root_msg", "Android does not let a normal app freeze (disable) other apps. Without root, run these commands from a computer over ADB:", "اینڈرائیڈ عام ایپ کو دوسری ایپس فریز (ڈس ایبل) کرنے کی اجازت نہیں دیتا۔ روٹ کے بغیر یہ کمانڈز کمپیوٹر سے ADB کے ذریعے چلائیں:", "لا يسمح أندرويد لتطبيق عادي بتجميد (تعطيل) تطبيقات أخرى. بدون روت، نفّذ هذه الأوامر من حاسوب عبر ADB:");
        a("copy", "Copy", "کاپی", "نسخ");
        a("copied", "Copied", "کاپی ہو گیا", "تم النسخ");
        a("lang_title", "Language", "زبان", "اللغة");
        a("theme_title", "Theme", "تھیم", "المظهر");
        a("dark", "Dark", "ڈارک", "داكن");
        a("light", "Light", "لائٹ", "فاتح");
        a("save", "Save", "محفوظ کریں", "حفظ");
        a("saved", "Saved", "محفوظ ہو گیا", "تم الحفظ");
        a("privacy_title", "Privacy and limits", "پرائیویسی اور حدود", "الخصوصية والقيود");
        a("privacy", "This app has no internet permission. Nothing leaves your phone. The accessibility service only looks at the App Info screen while Force stop automation is running.\n\nLimits: Android cannot tell normal apps which apps are running, so \"Not stopped\" means only that the app has not been force-stopped. Kill background cannot stop apps with a foreground service and they may restart. Force stop without root works by pressing the button on the App Info screen for you, so it depends on your phone's Settings app and may not work on every phone or language. Freeze needs root or ADB.", "اس ایپ کے پاس انٹرنیٹ کی اجازت نہیں ہے۔ کوئی چیز آپ کے فون سے باہر نہیں جاتی۔ ایکسیسبیلٹی سروس صرف فورس اسٹاپ آٹومیشن کے دوران ایپ انفو اسکرین دیکھتی ہے۔\n\nحدود: اینڈرائیڈ عام ایپس کو نہیں بتاتا کہ کون سی ایپس چل رہی ہیں، اس لیے \"رکی نہیں\" کا مطلب صرف یہ ہے کہ ایپ فورس اسٹاپ نہیں ہوئی۔ بیک گراؤنڈ بند کرنا فورگراؤنڈ سروس والی ایپس کو نہیں روک سکتا اور وہ دوبارہ چل سکتی ہیں۔ روٹ کے بغیر فورس اسٹاپ آپ کے لیے ایپ انفو اسکرین کا بٹن دباتا ہے، اس لیے یہ آپ کے فون کی سیٹنگز ایپ پر منحصر ہے اور ہر فون یا زبان میں نہیں چل سکتا۔ فریز کے لیے روٹ یا ADB چاہیے۔", "لا يملك هذا التطبيق إذن الإنترنت. لا يغادر شيء هاتفك. تنظر خدمة إمكانية الوصول فقط إلى شاشة معلومات التطبيق أثناء أتمتة الإيقاف الإجباري.\n\nالقيود: لا يخبر أندرويد التطبيقات العادية بالتطبيقات العاملة، لذا \"غير متوقف\" يعني فقط أنه لم يتم إيقافه إجباريًا. إنهاء الخلفية لا يوقف التطبيقات ذات الخدمة في الواجهة وقد تعيد التشغيل. الإيقاف الإجباري بدون روت يضغط الزر في شاشة معلومات التطبيق نيابة عنك، لذا يعتمد على تطبيق الإعدادات في هاتفك وقد لا يعمل على كل هاتف أو لغة. التجميد يحتاج روت أو ADB.");
    }

    static String t(Context c, String key) {
        String[] v = M.get(key);
        if (v == null) return key;
        String l = Store.lang(c);
        int i = 0;
        if ("ur".equals(l)) i = 1;
        else if ("ar".equals(l)) i = 2;
        return v[i];
    }
}
