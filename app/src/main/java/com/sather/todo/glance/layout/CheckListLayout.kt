package com.example.platform.ui.appwidgets.glance.layout.collections.layout

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.appwidget.components.CircleIconButton
import androidx.glance.appwidget.components.Scaffold
import androidx.glance.appwidget.components.TitleBar
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutDimensions.checkListRowEndPadding
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutDimensions.checkListRowStartPadding
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutDimensions.scaffoldHorizontalPadding
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutDimensions.verticalItemSpacing
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutDimensions.widgetPadding
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutSize.Companion.showTitleBar
import com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayoutSize.Small
import com.sather.todo.data.Routine

/**
 *一种布局，侧重于在检查列表中显示项目列表。内容以
 * [Scaffold] 位于特定于应用程序的标题栏下方。
 *
 * 布局是 [ActionListLayout] 的变体，其中 on-check，项从列表中删除一次
 * 在后端更新。布局假定清单项遵循特定顺序，因此，在
 * 较大的大小，布局显示额外的尾随，而不是在网格中显示项目
 * 每个项目的作。但是，如果网格适用于您的使用案例，您可以切换到它以
 * 支持大尺寸。
 *
 * 在此示例中，用户会看到 checked 状态，直到该项目从
 * 后备数据库。通过显示选中状态，用户可以清楚地知道点按
 *东西。具体来说，对于外观相似的文本，它可能不会立即对
 * 用户，该项目已被删除;中间 checked 状态提供了这种清晰度。
 *
 * @param要显示为 widget 标题的文本命名，例如您的 widget 或 app 的名称。
 * @param titleIconRes，一个代表您的应用或品牌的可着色图标，可以显示该图标
 * 使用提供的 [title]。在此示例中，我们使用 drawable 中的 icon
 * 资源，但您应该为您的用例使用适当的图标源。
 * @param可着色图标的 titleBarActionIconRes 资源 ID，该图标可显示为
 * 小部件标题栏区域中的图标按钮。为
 * 示例，用于启动搜索以查找特定
 *项目。
 * @param [titleBarActionIconRes] 按钮的 titleBarActionIconContentDescription 说明
 * 供辅助功能服务使用。
 * @param 单击[titleBarActionIconRes]按钮时执行的titleBarAction动作。
 * @param要包含在列表中的项目列表;通常包括一个简短的标题和一个
 * 支持文本。
 * @param checkedItems 处于选中状态的项的键列表。
 * @param按下 checkedIconRes 着色图标以取消选中项目。
 * @param按下 unCheckedIconRes 着色图标来检查项目。
 * @param checkButtonContentDescription 要使用的未选中按钮的描述
 * 无障碍服务;在选中状态下，Button 为
 * 不可点击。
 * @param onCheck 处理程序在点击图标时执行特定动作;在
 * 待办事项列表 这可以是从列表中删除项目的处理程序。

*
 * @see [CheckListItem] 表示接受的输入。
 * @see [com.example.platform.ui.appwidgets.glance.layout.collections.CheckListAppWidgetReceiver]
 */
@Composable
fun CheckListLayout(
  title: String,
  @DrawableRes titleIconRes: Int,
  @DrawableRes titleBarActionIconRes: Int,
  titleBarActionIconContentDescription: String,
  items: List<Routine>,
  checkedItems: List<Long>,
  @DrawableRes checkedIconRes: Int,
  @DrawableRes unCheckedIconRes: Int,
  checkButtonContentDescription: String,
  onCheck: (Long) -> Unit,
) {
  val checkListLayoutSize = CheckListLayoutSize.fromLocalSize()

  fun titleBar(): @Composable (() -> Unit) = {
    TitleBar(
      startIcon = ImageProvider(titleIconRes),
      // Based on your widget content, you may skip the title in smaller sizes.
      title = title.takeIf { checkListLayoutSize != Small } ?: "",
      iconColor = GlanceTheme.colors.primary,
      textColor = GlanceTheme.colors.onSurface,
      actions = {
        CircleIconButton(
          imageProvider = ImageProvider(titleBarActionIconRes),
          contentDescription = titleBarActionIconContentDescription,
          contentColor = GlanceTheme.colors.secondary,
          backgroundColor = null, // transparent
          onClick = {}
        )
      }
    )
  }

  val scaffoldTopPadding = if (showTitleBar()) {
    0.dp
  } else {
    widgetPadding
  }

  Scaffold(
    backgroundColor = GlanceTheme.colors.widgetBackground,
    horizontalPadding = scaffoldHorizontalPadding,
    modifier = GlanceModifier.padding(
      top = scaffoldTopPadding,
      bottom = widgetPadding
    ),
    titleBar = if (showTitleBar()) {
      titleBar()
    } else {
      null
    }
  ) {
    if (items.isEmpty()) {
      Text("items.isEmpty")
    } else {
      Content(
        items = items,
        checkedItems = checkedItems,
        onCheck = onCheck,
        checkedIconRes = checkedIconRes,
        unCheckedIconRes = unCheckedIconRes,
        checkButtonContentDescription = checkButtonContentDescription,
      )
    }
  }
}

@Composable
private fun Content(
  items: List<Routine>,
  checkedItems: List<Long>,
  onCheck: (Long) -> Unit,
  @DrawableRes checkedIconRes: Int,
  @DrawableRes unCheckedIconRes: Int,
  checkButtonContentDescription: String,
) {
  RoundedScrollingLazyColumn(
    modifier = GlanceModifier.fillMaxSize(),
    items = items,
    verticalItemsSpacing = verticalItemSpacing,
    itemContentProvider = { item ->
      CheckListItem(
        item = item,
        isChecked = checkedItems.contains(item.id),
        onCheck = onCheck,
        checkedIconRes = checkedIconRes,
        unCheckedIconRes = unCheckedIconRes,
        checkButtonContentDescription = checkButtonContentDescription,
      )
    }
  )
}

/**
 * A list item that displays a checkable item with a title, a supporting text, and trailing icon
 * buttons.
 *
 * Uses single line title (1-2 words), and 2-line supporting text (~ 50-55 characters)
 */
@Composable
private fun CheckListItem(
  item: Routine,
  @DrawableRes checkedIconRes: Int,
  @DrawableRes unCheckedIconRes: Int,
  checkButtonContentDescription: String,
  onCheck: (Long) -> Unit,
  modifier: GlanceModifier = GlanceModifier,
  isChecked: Boolean,
) {
    val listItemEndPadding = checkListRowEndPadding
    

  @Composable
  fun CheckButton() {
    CircleIconButton(
      imageProvider = if (isChecked) {
        ImageProvider(checkedIconRes)
      } else {
        ImageProvider(unCheckedIconRes)
      },
      backgroundColor = null, // to show transparent background
      contentColor = GlanceTheme.colors.secondary,
      contentDescription = checkButtonContentDescription,
      enabled = !isChecked,
      onClick = { onCheck(item.id) },
      key = "${LocalSize.current} ${item.id}"
    )
  }

  @Composable
  fun Title() {
    Text(
      text = item.content,
      style = CheckListLayoutTextStyles.titleText,
      maxLines = 2,
    )
  }

  @Composable
  fun SupportingText() {
    Text(
      text = item.subcontent,
      style = CheckListLayoutTextStyles.supportingText,
      maxLines = 2,
    )
  }

  // List item itself is not clickable, as it contains more trailing actions.
  ListItem(
      modifier = modifier.fillMaxWidth()
          .padding(start = checkListRowStartPadding, end = listItemEndPadding),
    contentSpacing = 0.dp, // Since check box's tap target covers the needed visual spacing
    leadingContent = { CheckButton() },
    headlineContent = { Title() },
    supportingContent = { SupportingText() },
  )
}


/**
 * Holds data corresponding to each item in a
 * [com.example.platform.ui.appwidgets.glance.layout.collections.layout.CheckListLayout].
 *
 * @param key a unique identifier for a specific item
 * @param title a short text (1-2 words) representing the item
 * @param supportingText a compact text (~50-55 characters) supporting the [title]; this allows
 *                       keeping the title short and glanceable, as well as helps support smaller
 *                       widget sizes.
 */
data class CheckListItem(
  val key: String,
  val title: String,
  val supportingText: String,
  val hasTrailingIcons: Boolean = false,
)

/**
 * Size of the widget per the reference breakpoints. Each size has its own display
 * characteristics such as - displaying containers on list items, font sizes, etc.
 *
 * In this layout, only width breakpoints are used to scale the layout.
 */
private enum class CheckListLayoutSize(val maxWidth: Dp) {
  // Smaller fonts, no title in title-bar
  Small(maxWidth = 260.dp),

  // larger fonts, title present, no trailing actions
  Medium(maxWidth = 304.dp),

  // 1 trailing action
  Large(maxWidth = 348.dp),

  // 2 trailing actions
  XLarge(maxWidth = 396.dp),

  // 3 trailing actions
  XXLarge(maxWidth = Dp.Infinity);

  companion object {
    /**
     * Returns the corresponding [CheckListLayoutSize] to be considered for the current widget size.
     */
    @Composable
    fun fromLocalSize(): CheckListLayoutSize {
      val size = LocalSize.current

      CheckListLayoutSize.values().forEach {
        if (size.width < it.maxWidth) {
          return it
        }
      }
      throw IllegalStateException("No mapped size ")
    }

    @Composable
    fun showTitleBar(): Boolean {
      return LocalSize.current.height >= 180.dp
    }
  }
}

private object CheckListLayoutTextStyles {
  /**
   * Style for the text displayed as title within each item.
   */
  val titleText: TextStyle
    @Composable get() = TextStyle(
      fontWeight = FontWeight.Medium,
      fontSize = if (CheckListLayoutSize.fromLocalSize() == Small) {
        14.sp // M3 Title Small
      } else {
        16.sp // M3 Title Medium
      },
      color = GlanceTheme.colors.onSurface
    )

  /**
   * Style for the text displayed as supporting text within each item.
   */
  val supportingText: TextStyle
    @Composable get() =
      TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp, // M3 Label Medium
        color = GlanceTheme.colors.secondary
      )
}

private object CheckListLayoutDimensions {
  val widgetPadding = 12.dp

  val verticalItemSpacing = 4.dp

  // Full width scrollable content
  val scaffoldHorizontalPadding = 0.dp
  // Match with the padding applied to the app icon in title bar; this allow us to vertically align
  // the app icon with check icon button.
  val checkListRowStartPadding = 2.dp
  // Padding to be applied on right of each item if there isn't a icon button on right.
  val checkListRowEndPadding = widgetPadding
}

/**
 * Preview sizes of layout at the configured width breakpoints.
 */
/**
 * Previews for the check list layout.
 *
 * First we look at the previews at defined breakpoints, tweaking them as necessary. In addition,
 * the previews at standard sizes allows us to quickly verify updates across min / max and common
 * widget sizes without needing to run the app or manually place the widget.
 */