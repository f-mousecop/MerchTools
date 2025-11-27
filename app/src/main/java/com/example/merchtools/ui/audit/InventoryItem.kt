package com.example.merchtools.ui.audit

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merchtools.R
import com.example.merchtools.components.QuantityStepper
import com.example.merchtools.domain.model.AuditItem
import com.example.merchtools.domain.model.Sku

@Composable
fun InventoryItem(
    item: AuditItem,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(dimensionResource(R.dimen.padding_medium))
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier,
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))

            ) {
                item.sku?.upc?.let {
                    Text(
                        text = "UPC: $it",
                        style = MaterialTheme.typography.titleMedium,)
                }

                Row() {
                    item.sku?.brand?.let {
                        Text(
                            text = it + " ${item.sku.casePack}"
                        )
                    }
                    Spacer(Modifier.widthIn(8.dp))
                    Text(
                        text = " Count: ${item.count}"
                    )
                }


                Text(
                    text = "Note: ${item.note}"
                )
            }
            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.padding(dimensionResource(R.dimen.padding_medium)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
            ) {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    Card(
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                "Photo",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                    Card(
                        modifier = Modifier
                            .width(64.dp)
                            .height(64.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                "Photo",
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun InventoryItemPreview() {
    InventoryItem(
        item = AuditItem(
            auditId = 2,
            skuId = 1,
            count = 3,
            note = "",
            sku = Sku(
                skuId = 1,
                upc = "1245511",
                name = "test",
                casePack = null,
                brand = "Pepsi"
            ),
        )
    )
}