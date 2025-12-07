package com.example.merchtools.ui.report

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.merchtools.R
import com.example.merchtools.components.BarcodeImage
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.util.BarcodeGenerator

@Composable
fun ReportItem(
    item: AuditItem,
    modifier: Modifier = Modifier,
    barcodeGenerator: BarcodeGenerator,
) {
    Card(
        modifier = modifier.height(180.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(4.dp),
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))

            ) {
                item.sku?.upc?.let {
                    Text(
                        text = "UPC: $it",
                        style = MaterialTheme.typography.titleMedium,)
                }

                Text(
                    text = "SKU: ${item.sku?.name ?: "—"}",
                    style = MaterialTheme.typography.titleSmall
                )


                val casePack = item.sku?.casePack ?: "—"
                item.sku?.brand?.let {
                    Text(
                        text = "$it $casePack",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Text(
                    text = "Count: ${item.count}"
                )


                Text(
                    text = "Note: ${item.note}",
                    modifier = Modifier
                        .wrapContentWidth(Alignment.Start),
                    maxLines = 4,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Column(
                modifier = Modifier
                    .padding(start = 16.dp, end = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Card(
                    modifier = Modifier
                        .size(128.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary),
                ) {
                    BarcodeImage(
                        upc = item.sku?.upc ?: "",
                        barcodeGenerator = barcodeGenerator,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}