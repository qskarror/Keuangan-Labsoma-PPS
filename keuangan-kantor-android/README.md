# Keuangan Kantor (Android)

Aplikasi pembungkus (WebView) untuk web app Apps Script "Keuangan Kantor".
APK dibangun otomatis oleh GitHub Actions.

## Cara pakai
1. Buat repository baru di GitHub, lalu unggah seluruh isi folder ini (termasuk folder `.github`).
2. Buka `app/src/main/res/values/strings.xml`, ganti `GANTI_DENGAN_ID_DEPLOYMENT` dengan URL web app Anda
   (Apps Script > Terapkan > Kelola penerapan > URL aplikasi web). Simpan/commit.
3. Buka tab **Actions** > **Build APK**. Build berjalan otomatis setiap commit ke `main`,
   atau klik **Run workflow**.
4. Setelah selesai (hijau), buka run tersebut, bagian **Artifacts**, unduh `keuangan-kantor-apk`.
   Ekstrak zip-nya, lalu pasang `app-debug.apk` di HP.

## Catatan
- Deploy web app dengan akses "Siapa saja" agar tidak perlu login Google (halaman login Google menolak WebView).
- Mengubah `Index.html` / `Code.gs` tidak perlu membuat APK baru. Cukup deploy versi baru di Apps Script.
- APK perlu dibuat ulang hanya jika URL, ikon, atau nama aplikasi berubah.
