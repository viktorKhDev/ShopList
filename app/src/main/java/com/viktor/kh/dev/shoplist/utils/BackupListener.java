package com.viktor.kh.dev.shoplist.utils;

public interface BackupListener {
    void onBackupRead();
    void onPreBackup();
    void onError();
}
