//
//给出省略代码：
//itemsIndexed(items = tempRoutineList, key = { _, item -> item.id }) { index, item ->
//
//    val focusRequester = remember { FocusRequester() }
//    BacklogEditRow(
//        modifier = Modifier.focusRequester(focusRequester),
//        addRoutine = {sortIndex,it ->
//            tempRoutineList.add()
//            focusManager.moveFocus(FocusDirection.Down)
//        },
//        deleteRoutine = {
//            focusManager.moveFocus(FocusDirection.Up)
//            tempRoutineList.removeAt(index)
//
//        },
//    )
//    if (index == initIndex ) {
//        LaunchedEffect(Unit) {
//            focusRequester.requestFocus()
//            initIndex = -2
//        }
//    }
//}，为什么focusManager.moveFocus(FocusDirection.Up),focusManager.moveFocus(FocusDirection.Up)不起效