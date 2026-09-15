package ps.reso.instaeclipse.ui.component.faq

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import ps.reso.instaeclipse.R
import ps.reso.instaeclipse.ui.component.settings.SettingsSectionCard
import ps.reso.instaeclipse.ui.theme.AppSpacing

@Composable
fun FaqScreen(
    modifier: Modifier = Modifier
) {
    val uriHandler = LocalUriHandler.current

    val faqList = listOf(
        parseFaq(stringResource(R.string.faq_1)),
        parseFaq(stringResource(R.string.faq_2)),
        parseFaq(stringResource(R.string.faq_3)),
        parseFaq(stringResource(R.string.faq_4)),
        parseFaq(stringResource(R.string.faq_5)),
        parseFaq(stringResource(R.string.faq_6))
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = AppSpacing.md)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(AppSpacing.md)
    ) {
        Spacer(Modifier.height(AppSpacing.xxs))

        SettingsSectionCard(title = stringResource(R.string.frequently_asked_questions)) {
            faqList.forEachIndexed { index, (question, answer) ->
                if (index > 0) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f),
                        modifier = Modifier.padding(vertical = AppSpacing.xs)
                    )
                }
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = question,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        SettingsSectionCard(title = stringResource(R.string.module_not_working_title)) {
            Text(
                text = "If features are not applying or the app behaves unexpectedly, check the instructions below:",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(Modifier.height(AppSpacing.xs))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    Text(
                        text = "Rooted Devices",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Use JingMatrix's LSPosed for modern Android versions.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FilledTonalButton(
                        onClick = { uriHandler.openUri("https://github.com/JingMatrix/LSPosed/releases/latest") },
                        shape = MaterialTheme.shapes.large,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Get JingMatrix LSPosed")
                        Spacer(Modifier.width(AppSpacing.xs))
                        Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }

            Spacer(Modifier.height(AppSpacing.xs))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.large,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AppSpacing.md),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                ) {
                    Text(
                        text = "Non-Rooted Devices",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Install JingMatrix's LSPatch, then patch the Instagram APK following the Telegram guide.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
                    ) {
                        FilledTonalButton(
                            onClick = { uriHandler.openUri("https://github.com/JingMatrix/LSPatch/releases/latest") },
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("LSPatch")
                            Spacer(Modifier.width(AppSpacing.xxs))
                            Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                        FilledTonalButton(
                            onClick = { uriHandler.openUri("https://t.me/instaEclipse_discussion/158/5149") },
                            shape = MaterialTheme.shapes.large,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Telegram Guide")
                            Spacer(Modifier.width(AppSpacing.xxs))
                            Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        SettingsSectionCard(title = stringResource(R.string.help_community)) {
            Button(
                onClick = { uriHandler.openUri("https://github.com/ReSo7200/InstaEclipse") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_github_logo),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(AppSpacing.xs))
                Text(stringResource(R.string.btn_instaeclipse_github), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(AppSpacing.xxs))
                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
            }

            FilledTonalButton(
                onClick = { uriHandler.openUri("https://t.me/InstaEclipse") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = MaterialTheme.shapes.extraLarge
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_telegram_logo),
                    contentDescription = stringResource(R.string.cd_telegram_icon),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(AppSpacing.xs))
                Text(stringResource(R.string.btn_telegram_support), fontWeight = FontWeight.SemiBold)
                Spacer(Modifier.width(AppSpacing.xxs))
                Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = null, modifier = Modifier.size(16.dp))
            }
        }

        Spacer(Modifier.height(AppSpacing.md))
    }
}

private fun parseFaq(raw: String): Pair<String, String> {
    val clean = raw.replace("<b>", "").replace("</b>", "")
    val parts = clean.split("\n- ")
    return if (parts.size >= 2) {
        parts[0].trim() to parts[1].trim()
    } else {
        val lines = clean.lines()
        if (lines.size >= 2) {
            lines[0].trim() to lines.drop(1).joinToString("\n").removePrefix("- ").trim()
        } else {
            clean.trim() to ""
        }
    }
}
