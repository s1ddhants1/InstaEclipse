package ps.reso.instaeclipse.mods.misc;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import ps.reso.instaeclipse.R;
import ps.reso.instaeclipse.utils.i18n.I18n;
import ps.reso.instaeclipse.utils.log.ModuleLog;

public final class TextCopyDialogUtil {

    private static final Handler MAIN = new Handler(Looper.getMainLooper());

    private TextCopyDialogUtil() {}

    public static boolean isDarkTheme(Context ctx) {
        int night = ctx.getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        return night == Configuration.UI_MODE_NIGHT_YES;
    }

    public static GradientDrawable roundRect(int color, float radiusDp, Context ctx) {
        float r = radiusDp * ctx.getResources().getDisplayMetrics().density;
        GradientDrawable d = new GradientDrawable();
        d.setColor(color);
        d.setCornerRadius(r);
        return d;
    }

    public static Button makeButton(Context ctx, String label,
                                    int bgColor, int textColor, float dp) {
        Button btn = new Button(ctx);
        btn.setText(label);
        btn.setTextColor(textColor);
        btn.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setBackground(roundRect(bgColor, 14, ctx));
        btn.setAllCaps(false);
        btn.setPadding((int) (20 * dp), (int) (14 * dp), (int) (20 * dp), (int) (14 * dp));
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.topMargin = (int) (10 * dp);
        btn.setLayoutParams(lp);
        return btn;
    }

    public static void showCopyPopup(final Context ctx, final String text,
                                     final int titleResId, final int copyFullResId,
                                     final int toastResId) {
        MAIN.post(() -> {
            try {
                float dp = ctx.getResources().getDisplayMetrics().density;
                boolean dark = isDarkTheme(ctx);

                int sheetBg    = dark ? Color.parseColor("#1C1C1E") : Color.parseColor("#F2F2F7");
                int cardBg     = dark ? Color.parseColor("#2C2C2E") : Color.parseColor("#FFFFFF");
                int textPrim   = dark ? Color.WHITE                 : Color.parseColor("#1C1C1E");
                int accentBg   = Color.parseColor("#0A84FF");
                int secondBg   = dark ? Color.parseColor("#3A3A3C") : Color.parseColor("#E5E5EA");
                int secondText = dark ? Color.WHITE                 : Color.parseColor("#1C1C1E");
                int handleClr  = dark ? Color.parseColor("#48484A") : Color.parseColor("#C7C7CC");

                LinearLayout sheet = new LinearLayout(ctx);
                sheet.setOrientation(LinearLayout.VERTICAL);
                sheet.setBackground(roundRect(sheetBg, 20, ctx));
                int hPad = (int) (20 * dp);
                sheet.setPadding(hPad, (int) (12 * dp), hPad, (int) (28 * dp));

                View handle = new View(ctx);
                LinearLayout.LayoutParams handleLp = new LinearLayout.LayoutParams(
                        (int) (40 * dp), (int) (4 * dp));
                handleLp.gravity = Gravity.CENTER_HORIZONTAL;
                handleLp.bottomMargin = (int) (16 * dp);
                handle.setLayoutParams(handleLp);
                handle.setBackground(roundRect(handleClr, 2, ctx));
                sheet.addView(handle);

                TextView titleTv = new TextView(ctx);
                titleTv.setText(I18n.t(ctx, titleResId));
                titleTv.setTextColor(textPrim);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                titleTv.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                titleLp.bottomMargin = (int) (14 * dp);
                titleTv.setLayoutParams(titleLp);
                sheet.addView(titleTv);

                TextView previewTv = new TextView(ctx);
                previewTv.setText(text);
                previewTv.setTextColor(textPrim);
                previewTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                previewTv.setMaxLines(6);
                previewTv.setEllipsize(TextUtils.TruncateAt.END);
                int cardPad = (int) (14 * dp);
                previewTv.setPadding(cardPad, cardPad, cardPad, cardPad);
                previewTv.setBackground(roundRect(cardBg, 12, ctx));
                LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                cardLp.bottomMargin = (int) (6 * dp);
                previewTv.setLayoutParams(cardLp);
                sheet.addView(previewTv);

                Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                Button btnCopy = makeButton(ctx,
                        I18n.t(ctx, copyFullResId), accentBg, Color.WHITE, dp);
                btnCopy.setOnClickListener(v -> {
                    dialog.dismiss();
                    copyToClipboard(ctx, text, toastResId);
                });
                sheet.addView(btnCopy);

                Button btnSelect = makeButton(ctx,
                        I18n.t(ctx, R.string.ig_comment_select_part), secondBg, secondText, dp);
                btnSelect.setOnClickListener(v -> {
                    dialog.dismiss();
                    showSelectDialog(ctx, text, toastResId);
                });
                sheet.addView(btnSelect);

                dialog.setContentView(sheet);
                Window w = dialog.getWindow();
                if (w != null) {
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    w.setGravity(Gravity.BOTTOM);
                    w.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    WindowManager.LayoutParams wlp = w.getAttributes();
                    int margin = (int) (12 * dp);
                    wlp.x = margin;
                    wlp.y = margin;
                    w.setAttributes(wlp);
                }
                dialog.show();

            } catch (Throwable t) {
                ModuleLog.line("(IE|CopyPopup) ❌ Error: " + t.getMessage());
            }
        });
    }

    public static void showSelectDialog(final Context ctx, final String text, final int toastResId) {
        MAIN.post(() -> {
            try {
                float dp = ctx.getResources().getDisplayMetrics().density;
                boolean dark = isDarkTheme(ctx);

                int sheetBg   = dark ? Color.parseColor("#1C1C1E") : Color.parseColor("#F2F2F7");
                int cardBg    = dark ? Color.parseColor("#2C2C2E") : Color.parseColor("#FFFFFF");
                int textPrim  = dark ? Color.WHITE                 : Color.parseColor("#1C1C1E");
                int accentBg  = Color.parseColor("#0A84FF");
                int secondBg  = dark ? Color.parseColor("#3A3A3C") : Color.parseColor("#E5E5EA");
                int handleClr = dark ? Color.parseColor("#48484A") : Color.parseColor("#C7C7CC");

                LinearLayout sheet = new LinearLayout(ctx);
                sheet.setOrientation(LinearLayout.VERTICAL);
                sheet.setBackground(roundRect(sheetBg, 20, ctx));
                int hPad = (int) (20 * dp);
                sheet.setPadding(hPad, (int) (12 * dp), hPad, (int) (28 * dp));

                View handle = new View(ctx);
                LinearLayout.LayoutParams handleLp = new LinearLayout.LayoutParams(
                        (int) (40 * dp), (int) (4 * dp));
                handleLp.gravity = Gravity.CENTER_HORIZONTAL;
                handleLp.bottomMargin = (int) (16 * dp);
                handle.setLayoutParams(handleLp);
                handle.setBackground(roundRect(handleClr, 2, ctx));
                sheet.addView(handle);

                TextView titleTv = new TextView(ctx);
                titleTv.setText(I18n.t(ctx, R.string.ig_comment_select_title));
                titleTv.setTextColor(textPrim);
                titleTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                titleTv.setTypeface(null, Typeface.BOLD);
                LinearLayout.LayoutParams titleLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                titleLp.bottomMargin = (int) (14 * dp);
                titleTv.setLayoutParams(titleLp);
                sheet.addView(titleTv);

                EditText et = new EditText(ctx);
                et.setText(text);
                et.setTextColor(textPrim);
                et.setTextIsSelectable(true);
                et.setFocusableInTouchMode(true);
                et.setInputType(android.text.InputType.TYPE_NULL);
                et.setKeyListener(null);
                et.setHorizontallyScrolling(false);
                et.setMaxLines(8);
                et.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                int cardPad = (int) (14 * dp);
                et.setPadding(cardPad, cardPad, cardPad, cardPad);
                et.setBackground(roundRect(cardBg, 12, ctx));
                et.setSelection(0, text.length());
                LinearLayout.LayoutParams etLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                etLp.bottomMargin = (int) (6 * dp);
                et.setLayoutParams(etLp);
                sheet.addView(et);

                Dialog dialog = new Dialog(ctx);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

                Button btnCopySel = makeButton(ctx,
                        I18n.t(ctx, R.string.ig_comment_copy_selected), accentBg, Color.WHITE, dp);
                btnCopySel.setOnClickListener(v -> {
                    int s = et.getSelectionStart(), e = et.getSelectionEnd();
                    String sel = (s >= 0 && e > s) ? text.substring(s, e) : text;
                    dialog.dismiss();
                    copyToClipboard(ctx, sel, toastResId);
                });
                sheet.addView(btnCopySel);

                Button btnCopyAll = makeButton(ctx,
                        I18n.t(ctx, R.string.ig_mention_copy_all), secondBg,
                        dark ? Color.WHITE : Color.parseColor("#1C1C1E"), dp);
                btnCopyAll.setOnClickListener(v -> {
                    dialog.dismiss();
                    copyToClipboard(ctx, text, toastResId);
                });
                sheet.addView(btnCopyAll);

                dialog.setContentView(sheet);
                Window w = dialog.getWindow();
                if (w != null) {
                    w.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    w.setGravity(Gravity.BOTTOM);
                    w.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                            WindowManager.LayoutParams.WRAP_CONTENT);
                    WindowManager.LayoutParams wlp = w.getAttributes();
                    int margin = (int) (12 * dp);
                    wlp.x = margin;
                    wlp.y = margin;
                    w.setAttributes(wlp);
                }
                dialog.show();
                et.requestFocus();

            } catch (Throwable t) {
                ModuleLog.line("(IE|SelectDialog) ❌ Error: " + t.getMessage());
            }
        });
    }

    public static void copyToClipboard(final Context ctx, final String text, final int toastResId) {
        try {
            ClipboardManager cm = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
            if (cm != null) {
                cm.setPrimaryClip(ClipData.newPlainText("text", text));
                MAIN.post(() ->
                        Toast.makeText(ctx, I18n.t(ctx, toastResId), Toast.LENGTH_SHORT).show());
            }
        } catch (Throwable t) {
            ModuleLog.line("(IE|Copy) ❌ Clipboard copy failed: " + t.getMessage());
        }
    }
}
