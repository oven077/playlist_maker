package com.agermolin.playlistmaker.search.presentation.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agermolin.playlistmaker.R
import com.agermolin.playlistmaker.core.presentation.theme.YsDisplayRegular

@Composable
fun SearchTextField(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onFocusChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = colorResource(R.color.search_form_background_color),
                shape = RoundedCornerShape(8.dp),
            )
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(R.drawable.search),
            contentDescription = null,
            modifier = Modifier
                .padding(end = 10.dp)
                .size(24.dp),
            tint = colorResource(R.color.search_form_text_hint_color),
        )

        Box(modifier = Modifier.weight(1f)) {
            if (query.isEmpty()) {
                Text(
                    text = stringResource(R.string.search),
                    color = colorResource(R.color.search_form_text_hint_color),
                    style = TextStyle(
                        fontFamily = YsDisplayRegular,
                        fontSize = 16.sp,
                    ),
                )
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState -> onFocusChanged(focusState.isFocused) },
                textStyle = TextStyle(
                    color = colorResource(R.color.search_form_text_color),
                    fontSize = 16.sp,
                    fontFamily = YsDisplayRegular,
                ),
                singleLine = true,
                cursorBrush = SolidColor(colorResource(R.color.search_form_text_color)),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onSearch() }),
            )
        }

        if (query.isNotEmpty()) {
            IconButton(onClick = onClear) {
                Icon(
                    painter = painterResource(R.drawable.clear),
                    contentDescription = null,
                    tint = colorResource(R.color.search_form_text_hint_color),
                )
            }
        }
    }
}
