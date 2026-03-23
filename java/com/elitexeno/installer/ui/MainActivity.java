package com.elitexeno.installer.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elitexeno.installer.R;
import com.elitexeno.installer.core.InstallEngine;

public class MainActivity extends Activity {

    private Button installButton;
    private TextView statusText;
    private TextView percentText;
    private ProgressBar installProgress;
    private ObjectAnimator installPulseAnimator;

    private final BroadcastReceiver progressReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra(InstallEngine.EXTRA_PROGRESS, 0);
            String message = intent.getStringExtra(InstallEngine.EXTRA_MESSAGE);
            String state = intent.getStringExtra(InstallEngine.EXTRA_STATE);
            applyInstallState(progress, message, state);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        setContentView(R.layout.activity_main);

        installButton = findViewById(R.id.installButton);
        Button instagramButton = findViewById(R.id.instagramButton);
        Button githubButton = findViewById(R.id.githubButton);
        statusText = findViewById(R.id.statusText);
        percentText = findViewById(R.id.percentText);
        installProgress = findViewById(R.id.installProgress);
        ImageView logoView = findViewById(R.id.logoView);
        View aboutPanel = findViewById(R.id.aboutPanel);
        View capabilitiesPanel = findViewById(R.id.capabilitiesPanel);
        View installPanel = findViewById(R.id.installPanel);
        View compliancePanel = findViewById(R.id.compliancePanel);
        View creditsPanel = findViewById(R.id.creditsPanel);
        Button complianceButton = findViewById(R.id.complianceButton);

        if (logoView.getDrawable() instanceof Animatable) {
            ((Animatable) logoView.getDrawable()).start();
        }

        applyInstallState(0, "Ready to install securely", InstallEngine.STATE_IDLE);

        instagramButton.setOnClickListener(v -> openExternal("https://instagram.com/_echo.del.alma_"));
        githubButton.setOnClickListener(v -> openExternal("https://github.com/jeet1511"));
        complianceButton.setOnClickListener(v -> showComplianceDialog());

        setupButtonTouchFeedback(installButton);
        setupButtonTouchFeedback(instagramButton);
        setupButtonTouchFeedback(githubButton);
        setupButtonTouchFeedback(complianceButton);
        startIntroAnimations(logoView, aboutPanel, capabilitiesPanel, installPanel, compliancePanel, creditsPanel);
        startLogoIdleAnimation(logoView);
        startInstallPulseAnimation();

        installButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
                    && !getPackageManager().canRequestPackageInstalls()) {
                InstallerUI.showMessage(this, "Allow 'Install unknown apps' for this app to continue");
                Intent intent = new Intent(
                        Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                        Uri.parse("package:" + getPackageName())
                );
                startActivity(intent);
                return;
            }
            InstallEngine.startInstall(this);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(InstallEngine.ACTION_INSTALL_PROGRESS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(progressReceiver, filter, RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(progressReceiver, filter);
        }
    }

    @Override
    protected void onStop() {
        stopInstallPulseAnimation();
        try {
            unregisterReceiver(progressReceiver);
        } catch (IllegalArgumentException ignored) {
        }
        super.onStop();
    }

    private void applyInstallState(int progress, String message, String state) {
        int safeProgress = Math.max(0, Math.min(100, progress));
        animateProgressTo(safeProgress);
        percentText.setText(safeProgress + "%");
        statusText.setText(message == null ? "Preparing installation" : message);

        if (InstallEngine.STATE_INSTALLING.equals(state) || InstallEngine.STATE_PENDING.equals(state)) {
            installButton.setEnabled(false);
            installButton.setText("Installing...");
            stopInstallPulseAnimation();
            return;
        }

        if (InstallEngine.STATE_SUCCESS.equals(state)) {
            installButton.setEnabled(false);
            installButton.setText("Installed");
            stopInstallPulseAnimation();
            return;
        }

        installButton.setEnabled(true);
        installButton.setText("INSTALL ELITE XENO");
        startInstallPulseAnimation();
    }

    private void openExternal(String url) {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            startActivity(intent);
        } catch (Exception ignored) {
            InstallerUI.showMessage(this, "No browser app found");
        }
    }

    private void startIntroAnimations(View logoView, View aboutPanel, View capabilitiesPanel,
                                      View installPanel, View compliancePanel, View creditsPanel) {
        animateIn(logoView, 0L);
        animateIn(aboutPanel, 120L);
        animateIn(capabilitiesPanel, 210L);
        animateIn(installPanel, 300L);
        animateIn(compliancePanel, 390L);
        animateIn(creditsPanel, 480L);
    }

    private void animateIn(View view, long delay) {
        view.setAlpha(0f);
        view.setTranslationY(40f);
        view.animate()
                .alpha(1f)
                .translationY(0f)
                .setStartDelay(delay)
                .setDuration(420L)
                .setInterpolator(new DecelerateInterpolator())
                .start();
    }

    private void startLogoIdleAnimation(View logoView) {
        ObjectAnimator floatY = ObjectAnimator.ofFloat(logoView, "translationY", 0f, -10f, 0f);
        floatY.setDuration(2600L);
        floatY.setRepeatCount(ObjectAnimator.INFINITE);
        floatY.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleX = ObjectAnimator.ofFloat(logoView, "scaleX", 1f, 1.03f, 1f);
        scaleX.setDuration(2600L);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator scaleY = ObjectAnimator.ofFloat(logoView, "scaleY", 1f, 1.03f, 1f);
        scaleY.setDuration(2600L);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setInterpolator(new AccelerateDecelerateInterpolator());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(floatY, scaleX, scaleY);
        animatorSet.start();
    }

    private void setupButtonTouchFeedback(View button) {
        button.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                v.animate().scaleX(0.97f).scaleY(0.97f).setDuration(90L).start();
            } else if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction() == MotionEvent.ACTION_CANCEL) {
                v.animate().scaleX(1f).scaleY(1f).setDuration(120L).start();
            }
            return false;
        });
    }

    private void animateProgressTo(int targetProgress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(installProgress, "progress",
                installProgress.getProgress(), targetProgress);
        progressAnimator.setDuration(280L);
        progressAnimator.start();
    }

    private void startInstallPulseAnimation() {
        if (installPulseAnimator != null && installPulseAnimator.isRunning()) {
            return;
        }
        installPulseAnimator = ObjectAnimator.ofFloat(installButton, "alpha", 1f, 0.86f, 1f);
        installPulseAnimator.setDuration(1650L);
        installPulseAnimator.setRepeatMode(ValueAnimator.RESTART);
        installPulseAnimator.setRepeatCount(ValueAnimator.INFINITE);
        installPulseAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        installPulseAnimator.start();
    }

    private void stopInstallPulseAnimation() {
        if (installPulseAnimator != null) {
            installPulseAnimator.cancel();
        }
        installButton.setAlpha(1f);
    }

    private void showComplianceDialog() {
        StringBuilder details = new StringBuilder();
        details.append("Permission Use\n")
                .append("• REQUEST_INSTALL_PACKAGES is used only to submit your selected package to Android PackageInstaller.\n")
                .append("• Installation still requires explicit user confirmation on the system installer screen.\n\n")
                .append("Security Controls\n")
                .append("• Install callback is token-validated to prevent spoofed broadcasts.\n")
                .append("• Cleartext network traffic is disabled by policy.\n")
                .append("• App backup / extraction is disabled for installer security data.\n\n")
                .append("Data Handling\n")
                .append("• Installer processes local encrypted payload only for installation flow.\n")
                .append("• No silent install is performed. Android system confirmation is mandatory.");

        new AlertDialog.Builder(this)
                .setTitle("Compliance & Privacy")
                .setMessage(details.toString())
                .setPositiveButton("Open App Settings", (dialog, which) -> {
                    Intent settingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.parse("package:" + getPackageName()));
                    startActivity(settingsIntent);
                })
                .setNegativeButton("Close", null)
                .show();
    }
}