package com.example.merchtools.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.merchtools.R
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.util.BarcodeGenerator

/**
 * A Composable function that displays a single audit inventory item in a styled Card.
 * It shows details like the barcode, UPC, SKU, brand, count, and any associated notes.
 * The card can be made clickable to handle user interactions.
 *
 * @param item The [AuditItem] data to display.
 * @param modifier The [Modifier] to be applied to the Card.
 * @param barcodeGenerator The [BarcodeGenerator] used to create the barcode image.
 * @param onClick A lambda function to be executed when the card is clicked. Only active if [clickable] is true.
 * @param height The height of the card.
 * @param clickable A boolean to determine if the card should respond to click events.
 */
@Composable
fun AuditInventoryItem(
    item: AuditItem,
    modifier: Modifier = Modifier,
    barcodeGenerator: BarcodeGenerator,
    onClick: () -> Unit = {},
    height: Dp,
    clickable: Boolean
) {
    Card(
        modifier = modifier
            .height(height)
            .then(if (clickable) Modifier.clickable(onClick = onClick) else Modifier),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(4.dp),
        content = {
            AuditItemDetails(
                item = item,
                barcodeGenerator = barcodeGenerator
            )
        }
    )
}

@Composable
fun AuditItemDetails(
    item: AuditItem,
    modifier: Modifier = Modifier,
    barcodeGenerator: BarcodeGenerator
) {
    Column(
        modifier = modifier
            .padding(dimensionResource(R.dimen.padding_small))
            .fillMaxWidth()
    ) {
        Surface(
            shape = RoundedCornerShape(4.dp),
            tonalElevation = 2.dp,
            modifier = Modifier
                .height(88.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        ) {
            BarcodeImage(
                upc = item.sku?.upc ?: "",
                barcodeGenerator = barcodeGenerator,
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        Spacer(Modifier.height(dimensionResource(R.dimen.padding_small)))

        /*if (!(item.sku?.upc.isNullOrBlank())) {
            Text(
                text = "UPC: ${item.sku.upc}",
                style = MaterialTheme.typography.titleMedium,
            )
        }*/

        Text(
            text = "SKU: ${item.sku?.name ?: "—"}",
            style = MaterialTheme.typography.titleSmall
        )

        val casePack = item.sku?.casePack
        val brand = item.sku?.brand
        val brandInfo = listOfNotNull(brand, casePack).joinToString(" ")

        if (brandInfo.isNotBlank()) {
            Text(
                text = brandInfo,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Text(
            text = "Count: ${item.count}"
        )

        Text(
            text = "Note: ${item.note ?: "—"}",
            modifier = Modifier
                .wrapContentWidth(Alignment.Start),
            maxLines = 4,
            overflow = TextOverflow.Ellipsis
        )
    }
}