package ps.reso.instaeclipse.utils.toast;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ps.reso.instaeclipse.R;
import ps.reso.instaeclipse.utils.dialog.DialogUtils;
import ps.reso.instaeclipse.utils.feature.FeatureStatusTracker;
import ps.reso.instaeclipse.utils.log.ModuleLog;

public class CustomToast {

    public static boolean toastShown = false;

    private static class StatusIndicatorDrawable extends Drawable {
        public static final int TYPE_ACTIVE  = 0;
        public static final int TYPE_BROKEN  = 1;
        public static final int TYPE_PENDING = 2;

        private final int type;
        private final float d;
        private final Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Paint fillPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();

        StatusIndicatorDrawable(int type, float density) {
            this.type = type;
            this.d = density;

            strokePaint.setStyle(Paint.Style.STROKE);
            strokePaint.setStrokeCap(Paint.Cap.ROUND);
            strokePaint.setStrokeJoin(Paint.Join.ROUND);
            fillPaint.setStyle(Paint.Style.FILL);

            if (type == TYPE_ACTIVE) {
                strokePaint.setColor(Color.parseColor("#30D158"));
                strokePaint.setStrokeWidth(1.75f * d);
                fillPaint.setColor(Color.parseColor("#2630D158"));
            } else if (type == TYPE_BROKEN) {
                strokePaint.setColor(Color.parseColor("#FF453A"));
                strokePaint.setStrokeWidth(1.75f * d);
                fillPaint.setColor(Color.parseColor("#26FF453A"));
            } else {
                strokePaint.setColor(Color.parseColor("#636366"));
                strokePaint.setStrokeWidth(1.25f * d);
                fillPaint.setColor(Color.TRANSPARENT);
            }
        }

        @Override
        public void draw(Canvas canvas) {
            Rect bounds = getBounds();
            float cx = bounds.exactCenterX();
            float cy = bounds.exactCenterY();
            float r = Math.min(bounds.width(), bounds.height()) / 2f;
            if (r <= 0) return;

            if (type == TYPE_ACTIVE) {
                canvas.drawCircle(cx, cy, r - 0.5f * d, fillPaint);

                path.reset();
                float scale = r / (6.5f * d);
                path.moveTo(cx - 2.8f * d * scale, cy + 0.1f * d * scale);
                path.lineTo(cx - 0.7f * d * scale, cy + 2.3f * d * scale);
                path.lineTo(cx + 3.0f * d * scale, cy - 2.3f * d * scale);
                canvas.drawPath(path, strokePaint);
            } else if (type == TYPE_BROKEN) {
                canvas.drawCircle(cx, cy, r - 0.5f * d, fillPaint);

                float offset = 2.2f * d * (r / (6.5f * d));
                canvas.drawLine(cx - offset, cy - offset, cx + offset, cy + offset, strokePaint);
                canvas.drawLine(cx + offset, cy - offset, cx - offset, cy + offset, strokePaint);
            } else {
                canvas.drawCircle(cx, cy, r - 0.8f * d, strokePaint);
            }
        }

        @Override
        public void setAlpha(int alpha) {
            strokePaint.setAlpha(alpha);
            fillPaint.setAlpha((int) (alpha * 0.15f));
        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {
            strokePaint.setColorFilter(colorFilter);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return (int) (13 * d);
        }

        @Override
        public int getIntrinsicHeight() {
            return (int) (13 * d);
        }
    }

    public static void showCustomToast(Context context, String message) {
        if (context == null || message == null || message.isEmpty()) {
            ModuleLog.line("CustomToast: Context or message is null/empty!");
            return;
        }

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                final float d = context.getResources().getDisplayMetrics().density;

                FrameLayout wrap = new FrameLayout(context);
                int m = (int) (16 * d);
                wrap.setPadding(m, 0, m, 0);

                LinearLayout pill = new LinearLayout(context);
                pill.setOrientation(LinearLayout.HORIZONTAL);
                pill.setGravity(Gravity.CENTER_VERTICAL);

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Color.parseColor("#F0262626"));
                bg.setCornerRadius(22 * d);
                bg.setStroke(Math.max(1, (int) d), Color.parseColor("#26FFFFFF"));
                pill.setBackground(bg);
                pill.setPadding((int) (18 * d), (int) (10 * d), (int) (18 * d), (int) (10 * d));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    pill.setElevation(8 * d);
                }

                TextView toastText = new TextView(context);
                toastText.setText(message);
                toastText.setTextColor(Color.WHITE);
                toastText.setTextSize(13.5f);
                toastText.setTypeface(null, Typeface.BOLD);
                toastText.setGravity(Gravity.CENTER);
                pill.addView(toastText);

                FrameLayout.LayoutParams pillLp = new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_HORIZONTAL);
                pill.setLayoutParams(pillLp);
                wrap.addView(pill);

                Toast toast = new Toast(context);
                toast.setView(wrap);
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, (int) (84 * d));
                toast.show();
            } catch (Throwable t) {
                ModuleLog.line("Failed to show custom toast: " + Log.getStackTraceString(t));
            }
        });
    }

    private static final String[] CAT_ORDER = {"Appearance", "Privacy", "Media", "Tools"};

    private static String categoryOf(String key) {
        switch (key) {
            case "CustomTheme": case "CustomFont": case "CustomEmoji": case "ForceReelQuality":
            case "HideSuggestionsInFeed": case "HideThreadsSuggestions":
                return "Appearance";
            case "GhostSeen": case "GhostTyping": case "GhostStories": case "GhostLive":
            case "GhostViewOnce": case "GhostScreenshot": case "AllowScreenshots":
            case "KeepEphemeralMessages": case "KeepUnsentMessages": case "PermanentViewMode":
            case "LockDirectMessages": case "HideSpecificChats": case "AdBlocker":
            case "DisableTrackingLinks": case "RemoveMetaAI": case "SpoofLastSeen":
            case "DisableDiscoverPeople":
                return "Privacy";
            case "PostDownload": case "ReelDownload": case "StoryDownload": case "ProfileDownload":
            case "CopyMediaLink": case "SaveInstants": case "UploadInstants": case "CacheStories":
            case "SpoofLocation": case "StoryMentions": case "CaptionCopy": case "CopyComment":
            case "PhotoZoom":
                return "Media";
            default:
                return "Tools";
        }
    }

    public static void showFeatureToast(Context context, String title,
                                        LinkedHashMap<String, Boolean> status) {
        showFeatureGrid(context, title, status);
    }

    public static void showFeatureGrid(Context context, String title,
                                       LinkedHashMap<String, Boolean> status) {
        if (context == null || status == null || status.isEmpty()) return;

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                final float d = context.getResources().getDisplayMetrics().density;
                final int off = Color.parseColor("#8E8E93");
                final int txt = Color.parseColor("#F2F2F7");

                LinkedHashMap<String, List<String>> byCat = new LinkedHashMap<>();
                for (String c : CAT_ORDER) byCat.put(c, new ArrayList<>());
                int active = 0;
                int brokenCount = 0;
                for (Map.Entry<String, Boolean> e : status.entrySet()) {
                    byCat.get(categoryOf(e.getKey())).add(e.getKey());
                    if (Boolean.TRUE.equals(e.getValue())) {
                        active++;
                    } else if (FeatureStatusTracker.isBroken(e.getKey())) {
                        brokenCount++;
                    }
                }

                LinearLayout card = new LinearLayout(context);
                card.setOrientation(LinearLayout.VERTICAL);

                GradientDrawable bg = new GradientDrawable();
                bg.setColor(Color.parseColor("#FA262626"));
                bg.setCornerRadius(22 * d);
                bg.setStroke(Math.max(1, (int) d), Color.parseColor("#26FFFFFF"));
                card.setBackground(bg);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    card.setElevation(10 * d);
                }
                card.setPadding((int) (14 * d), (int) (12 * d), (int) (14 * d), (int) (12 * d));

                LinearLayout headerRow = new LinearLayout(context);
                headerRow.setOrientation(LinearLayout.HORIZONTAL);
                headerRow.setGravity(Gravity.CENTER_VERTICAL);
                headerRow.setPadding(0, 0, 0, (int) (4 * d));

                ImageView iconView = new ImageView(context);
                Drawable logo = null;
                try {
                    logo = DialogUtils.moduleIcon(R.drawable.ic_logo, Color.WHITE);
                } catch (Throwable ignored) {}
                if (logo == null) {
                    try {
                        logo = ContextCompat.getDrawable(context, R.drawable.ic_logo);
                    } catch (Throwable ignored) {}
                }
                if (logo != null) {
                    iconView.setImageDrawable(logo);
                    iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(
                            (int) (18 * d), (int) (18 * d));
                    iconLp.rightMargin = (int) (8 * d);
                    iconView.setLayoutParams(iconLp);
                    headerRow.addView(iconView);
                }

                TextView header = new TextView(context);
                header.setText(title != null && !title.isEmpty() ? title : "InstaEclipse Loaded");
                header.setTextColor(Color.WHITE);
                header.setTextSize(14.5f);
                header.setTypeface(null, Typeface.BOLD);
                header.setLetterSpacing(0.01f);
                headerRow.addView(header);

                View spacer = new View(context);
                LinearLayout.LayoutParams spLp = new LinearLayout.LayoutParams(0, 0, 1f);
                spacer.setLayoutParams(spLp);
                headerRow.addView(spacer);

                TextView badge = new TextView(context);
                badge.setText(active + "/" + status.size() + " Active");
                int badgeColor = (brokenCount > 0) ? Color.parseColor("#FF453A")
                        : (active == status.size()) ? Color.parseColor("#30D158")
                        : Color.WHITE;
                badge.setTextColor(badgeColor);
                badge.setTextSize(11);
                badge.setTypeface(null, Typeface.BOLD);
                GradientDrawable badgeBg = new GradientDrawable();
                badgeBg.setColor(Color.parseColor("#22FFFFFF"));
                badgeBg.setCornerRadius(10 * d);
                badgeBg.setStroke(Math.max(1, (int) d), Color.parseColor("#1FFFFFFF"));
                badge.setBackground(badgeBg);
                badge.setPadding((int) (8 * d), (int) (3 * d), (int) (8 * d), (int) (3 * d));
                headerRow.addView(badge);

                card.addView(headerRow);

                View divider = new View(context);
                divider.setBackgroundColor(Color.parseColor("#1AFFFFFF"));
                LinearLayout.LayoutParams divLp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT, (int) (1 * d));
                divLp.topMargin = (int) (6 * d);
                divLp.bottomMargin = (int) (4 * d);
                divider.setLayoutParams(divLp);
                card.addView(divider);

                for (String cat : CAT_ORDER) {
                    List<String> keys = byCat.get(cat);
                    if (keys == null || keys.isEmpty()) continue;

                    TextView catHeader = new TextView(context);
                    catHeader.setText(cat.toUpperCase(Locale.getDefault()));
                    catHeader.setTextColor(Color.parseColor("#8E8E93"));
                    catHeader.setTextSize(10.5f);
                    catHeader.setTypeface(null, Typeface.BOLD);
                    catHeader.setLetterSpacing(0.08f);
                    catHeader.setPadding(0, (int) (6 * d), 0, (int) (2 * d));
                    card.addView(catHeader);

                    GridLayout grid = new GridLayout(context);
                    grid.setColumnCount(2);
                    for (String key : keys) {
                        boolean hooked = Boolean.TRUE.equals(status.get(key));
                        boolean brokenState = !hooked && FeatureStatusTracker.isBroken(key);

                        LinearLayout cell = new LinearLayout(context);
                        cell.setOrientation(LinearLayout.HORIZONTAL);
                        cell.setGravity(Gravity.CENTER_VERTICAL);
                        int cp = (int) (2.5f * d);
                        cell.setPadding(0, cp, (int) (6 * d), cp);

                        ImageView mark = new ImageView(context);
                        int statusType = hooked ? StatusIndicatorDrawable.TYPE_ACTIVE
                                : brokenState ? StatusIndicatorDrawable.TYPE_BROKEN
                                : StatusIndicatorDrawable.TYPE_PENDING;
                        mark.setImageDrawable(new StatusIndicatorDrawable(statusType, d));
                        int iconSize = (int) (13 * d);
                        LinearLayout.LayoutParams markLp = new LinearLayout.LayoutParams(iconSize, iconSize);
                        markLp.rightMargin = (int) (5 * d);
                        mark.setLayoutParams(markLp);
                        cell.addView(mark);

                        TextView lbl = new TextView(context);
                        lbl.setText(FeatureStatusTracker.getLabel(context, key));
                        lbl.setTextColor(hooked ? txt : brokenState ? Color.parseColor("#FF453A") : off);
                        lbl.setTextSize(11.5f);
                        lbl.setLetterSpacing(-0.01f);
                        lbl.setMaxLines(1);
                        lbl.setEllipsize(TextUtils.TruncateAt.END);
                        cell.addView(lbl);

                        GridLayout.LayoutParams lp = new GridLayout.LayoutParams();
                        lp.width = 0;
                        lp.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
                        lp.setGravity(Gravity.FILL_HORIZONTAL);
                        cell.setLayoutParams(lp);
                        grid.addView(cell);
                    }
                    card.addView(grid);
                }

                FrameLayout wrap = new FrameLayout(context);
                int m = (int) (10 * d);
                wrap.setPadding(m, 0, m, 0);
                int screenW = context.getResources().getDisplayMetrics().widthPixels;
                card.setLayoutParams(new FrameLayout.LayoutParams(
                        screenW - 2 * m, FrameLayout.LayoutParams.WRAP_CONTENT));
                wrap.addView(card);

                Toast toast = new Toast(context);
                toast.setView(wrap);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, (int) (70 * d));
                toast.show();

                new Handler(Looper.getMainLooper()).postDelayed(toast::show, 3200);
                new Handler(Looper.getMainLooper()).postDelayed(toast::cancel, 6500);
            } catch (Throwable t) {
                ModuleLog.line("showFeatureGrid failed: " + Log.getStackTraceString(t));
            }
        });
    }
}
