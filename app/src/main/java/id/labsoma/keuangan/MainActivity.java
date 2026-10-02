package id.labsoma.keuangan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends Activity {

    // Menyembunyikan banner "Aplikasi ini dibuat oleh pengguna Google Apps Script".
    // Banner dipasang Google di halaman luar; aplikasi kita berada di dalam iframe.
    // Semua elemen halaman luar selain iframe disembunyikan, lalu iframe dibuat memenuhi layar.
    private static final String HIDE_BANNER_JS =
            "(function(){"
            + "if(window.__kkHide)return;window.__kkHide=1;"
            + "function run(){"
            + "var f=document.getElementById('userHtmlFrame')||document.querySelector('iframe');"
            + "if(!f||!document.body)return;"
            + "var keep=[];"
            + "for(var n=f;n&&n!==document.documentElement;n=n.parentElement)keep.push(n);"
            + "var all=document.body.querySelectorAll('*');"
            + "for(var i=0;i<all.length;i++){"
            + "var e=all[i];"
            + "if(keep.indexOf(e)>=0)continue;"
            + "var t=e.tagName;"
            + "if(t==='SCRIPT'||t==='STYLE'||t==='LINK'||t==='META'||t==='NOSCRIPT')continue;"
            + "e.style.setProperty('display','none','important');"
            + "}"
            + "var st=f.style;"
            + "st.setProperty('position','fixed','important');"
            + "st.setProperty('top','0','important');"
            + "st.setProperty('left','0','important');"
            + "st.setProperty('width','100%','important');"
            + "st.setProperty('height','100%','important');"
            + "st.setProperty('border','0','important');"
            + "document.documentElement.style.setProperty('overflow','hidden','important');"
            + "document.body.style.setProperty('overflow','hidden','important');"
            + "}"
            + "run();"
            + "new MutationObserver(run).observe(document.documentElement,{childList:true,subtree:true});"
            + "})();";

    private WebView web;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        web = new WebView(this);
        setContentView(web);

        WebSettings s = web.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setUseWideViewPort(true);          // ikuti meta viewport halaman
        s.setLoadWithOverviewMode(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setTextZoom(100);                  // abaikan ukuran font sistem agar tata letak stabil
        s.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);

        CookieManager cm = CookieManager.getInstance();
        cm.setAcceptCookie(true);
        cm.setAcceptThirdPartyCookies(web, true);

        web.setOverScrollMode(View.OVER_SCROLL_NEVER);
        web.setHorizontalScrollBarEnabled(false);

        web.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url != null && url.contains("script.google.com")) {
                    view.evaluateJavascript(HIDE_BANNER_JS, null);
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri u = request.getUrl();
                if (isInternal(u)) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, u));
                } catch (Exception ignored) { }
                return true;
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if (request.isForMainFrame()) {
                    String url = getString(R.string.app_url);
                    String html = "<html><head><meta name='viewport' content='width=device-width,initial-scale=1'></head>"
                            + "<body style='font-family:sans-serif;text-align:center;padding:48px 24px;color:#18312b'>"
                            + "<h2>Tidak dapat terhubung</h2>"
                            + "<p>Periksa koneksi internet Anda.</p>"
                            + "<p><a href='" + url + "' style='display:inline-block;padding:12px 20px;"
                            + "background:#176b52;color:#fff;border-radius:8px;text-decoration:none'>Coba lagi</a></p>"
                            + "</body></html>";
                    view.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
                }
            }
        });

        // Tanpa ini, alert() dan confirm() (mis. konfirmasi hapus) tidak muncul / selalu dianggap "batal".
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (d, w) -> result.confirm())
                        .setOnCancelListener(d -> result.cancel())
                        .show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(message)
                        .setPositiveButton(android.R.string.ok, (d, w) -> result.confirm())
                        .setNegativeButton(android.R.string.cancel, (d, w) -> result.cancel())
                        .setOnCancelListener(d -> result.cancel())
                        .show();
                return true;
            }
        });

        if (savedInstanceState != null) {
            web.restoreState(savedInstanceState);
        } else {
            web.loadUrl(getString(R.string.app_url));
        }
    }

    private boolean isInternal(Uri u) {
        String host = u.getHost();
        if (host == null) return false;
        return host.equals("google.com") || host.endsWith(".google.com")
                || host.endsWith(".googleusercontent.com")
                || host.endsWith(".gstatic.com");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        web.saveState(outState);
    }

    @Override
    protected void onPause() {
        web.onPause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        web.onResume();
    }

    @Override
    public void onBackPressed() {
        if (web.canGoBack()) web.goBack();
        else super.onBackPressed();
    }
}
