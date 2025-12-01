package com.example.merchtools.ui.audit.edit_audit_item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.merchtools.R
import com.example.merchtools.components.BarcodeImage
import com.example.merchtools.domain.util.BarcodeGenerator
import com.example.merchtools.ui.theme.MerchToolsTheme

@Composable
fun SkuItem(
    upc: String,
    name: String,
    casePack: String,
    brand: String,
    barcodeGenerator: BarcodeGenerator,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
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
                    BarcodeImage(
                        upc = upc,
                        barcodeGenerator = barcodeGenerator,
                        modifier = modifier.fillMaxSize()
                    )
                }
                Spacer(modifier.width(dimensionResource(R.dimen.padding_small)))

                Column(
                    modifier = modifier
                        .padding(start = dimensionResource(R.dimen.padding_medium))
                        .align(Alignment.CenterVertically),
                    verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small))
                ) {
                    Text(
                        text = "Product: $brand $casePack",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "UPC: $upc",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "SKU: $name",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SkuItemPreview() {
    MerchToolsTheme() {
        /*SkuItem(
            upc = "123456789012",
            name = "123456",
            casePack = "12pk",
            brand = "Pepsi",
            barcodeGenerator = BarcodeGenerator
        )*/
    }
}