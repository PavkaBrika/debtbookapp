package com.breckneck.debtbook.core.ui.components

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.breckneck.debtbook.core.ui.theme.DebtBookTheme
import com.yandex.mobile.ads.banner.BannerAdEventListener
import com.yandex.mobile.ads.banner.BannerAdSize
import com.yandex.mobile.ads.banner.BannerAdView
import com.yandex.mobile.ads.common.AdRequest
import com.yandex.mobile.ads.common.AdRequestError
import com.yandex.mobile.ads.common.ImpressionData

/**
 * Composable wrapper for Yandex BannerAdView via AndroidView.
 * Mirrors the setup in MainActivity for a sticky-width banner.
 */
@Composable
fun AdBannerComposable(
    adUnitId: String = "R-M-1753297-1",
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bannerAdView = rememberBannerAdView(context = context, adUnitId = adUnitId)

    AndroidView(
        factory = { bannerAdView },
        modifier = modifier
    )
}

@Composable
private fun rememberBannerAdView(context: Context, adUnitId: String): BannerAdView {
    val density = context.resources.displayMetrics.density
    val screenWidthPx = context.resources.displayMetrics.widthPixels
    val adWidthDp = (screenWidthPx / density).toInt()

    val bannerAdView = remember {
        BannerAdView(context).apply {
            setAdUnitId(adUnitId)
            setAdSize(BannerAdSize.stickySize(context, adWidthDp))
            setBannerAdEventListener(object : BannerAdEventListener {
                override fun onAdLoaded() {}
                override fun onAdFailedToLoad(error: AdRequestError) {}
                override fun onAdClicked() {}
                override fun onLeftApplication() {}
                override fun onReturnedToApplication() {}
                override fun onImpression(data: ImpressionData?) {}
            })
            loadAd(AdRequest.Builder().build())
        }
    }

    DisposableEffect(bannerAdView) {
        onDispose { bannerAdView.destroy() }
    }

    return bannerAdView
}

@Preview(name = "AdBanner — placeholder")
@Composable
private fun AdBannerPreview() {
    DebtBookTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ad Banner (320×50)",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
