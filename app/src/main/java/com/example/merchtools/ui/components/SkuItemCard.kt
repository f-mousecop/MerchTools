package com.example.merchtools.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.example.merchtools.R
import com.example.merchtools.domain.model.Sku
import com.example.merchtools.ui.theme.MerchToolsTheme

/**
 * A Composable that displays information about a specific SKU (Stock Keeping Unit) in a card format.
 * It shows the product image, brand, case pack, UPC, and SKU name.
 *
 * @param sku The [Sku] object containing the data to display. Can be null, in which case placeholders are shown.
 * @param modifier The [Modifier] to be applied to the component.
 * @param onClick A lambda function to be executed when the card is clicked. Only active if [clickable] is true.
 * @param clickable A boolean to determine if the card should respond to click events.
 */
@Composable
fun SkuItemCard(
    sku: Sku?,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    clickable: Boolean,
) {
    Card(
        modifier = modifier
            .then(if (clickable) Modifier.clickable(onClick = onClick) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onSecondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondary
        )
    ) {
        Column(
            modifier = modifier
                .padding(dimensionResource(R.dimen.padding_small))
        ) {
            Row(
                modifier = modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    tonalElevation = 2.dp,
                    modifier = modifier.size(128.dp)
                ) {
                    val model = sku?.imageUri

                    if (model != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(model.toUri())
                                .crossfade(true)
                                .build(),
                            placeholder = painterResource(R.drawable.photo_64dp_placeholder),
                            error = painterResource(R.drawable.photo_64dp_placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        )
                    } else {
                        Image(
                            painter = painterResource(R.drawable.error_placeholder),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.clip(RoundedCornerShape(4.dp))
                        )
                    }
                }

                Spacer(modifier.width(dimensionResource(R.dimen.padding_small)))

                Column(
                    modifier = modifier
                        .padding(start = dimensionResource(R.dimen.padding_medium))
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    Text(
                        text = "Product: ${sku?.brand} ${sku?.casePack}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "UPC: ${sku?.upc}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "SKU: ${sku?.name}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkuItemCardPreview() {
    MerchToolsTheme() {
        SkuItemCard(
            sku = Sku(
                upc = "123444444444",
                name = "Test",
                brand = "Test",
                casePack = "Test",
                imageUri = ""
            ),
            onClick = {},
            clickable = true,
        )
    }
}