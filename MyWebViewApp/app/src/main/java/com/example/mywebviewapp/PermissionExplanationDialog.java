package com.example.mywebviewapp;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;

public class PermissionExplanationDialog extends DialogFragment {
    private static final String ARG_PERMISSION_TYPE = "permission_type";
    
    public static PermissionExplanationDialog newInstance(String permissionType) {
        PermissionExplanationDialog dialog = new PermissionExplanationDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PERMISSION_TYPE, permissionType);
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String permissionType = getArguments().getString(ARG_PERMISSION_TYPE);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        
        String title = "Permission Required";
        String message = getPermissionExplanation(permissionType);
        
        builder.setTitle(title)
               .setMessage(message)
               .setPositiveButton("Grant Permission", (dialog, id) -> {
                   handlePermissionRequest(permissionType);
               })
               .setNegativeButton("Cancel", (dialog, id) -> {
                   dismiss();
               });
        
        return builder.create();
    }

    private String getPermissionExplanation(String permissionType) {
        switch (permissionType) {
            case "notification":
                return "This permission is needed to process system notifications. " +
                       "We only process notifications you explicitly allow and do not store any notification data.";
            case "boot":
                return "This permission is needed to restart the app's services after your device reboots. " +
                       "This ensures the app continues to function properly after device restarts.";
            case "internet":
                return "Internet access is required to load web content. " +
                       "We don't track your browsing activity or collect any personal data.";
            default:
                return "This permission is required for core app functionality.";
        }
    }

    private void handlePermissionRequest(String permissionType) {
        if ("notification".equals(permissionType)) {
            // Open an in-app guide that explains how to enable notification access.
            // NOTE: Per Android security model, apps cannot enable this permission programmatically.
            // We must direct the user to the system settings and provide clear steps.
            Intent intent = new Intent(getActivity(), NotificationPermissionGuideActivity.class);
            startActivity(intent);
        }
        // Add other permission handling as needed
    }
}