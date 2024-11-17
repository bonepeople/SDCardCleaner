package com.bonepeople.android.sdcardcleaner.ui.home

sealed class HomeState {
    object Init : HomeState()
    object Ready : HomeState()
    object ScanExecuting : HomeState()
    object ScanStopping : HomeState()
    object ScanFinish : HomeState()
    object CleanExecuting : HomeState()
    object CleanStopping : HomeState()
    object CleanFinish : HomeState()
}
//when (state) {
//            FileTreeManager.STATE.READY -> {
//                views.textViewState.setText(R.string.state_ready)
//                views.buttonTop.setText(R.string.caption_button_startScan)
//                views.buttonTop.singleClick { startScan() }
//                views.buttonTop.show()
//                views.buttonLeft.gone()
//                views.buttonRight.gone()
//            }
//
//            FileTreeManager.STATE.SCAN_EXECUTING -> {
//                views.textViewState.setText(R.string.state_scan_executing)
//                views.buttonTop.setText(R.string.caption_button_stopScan)
//                views.buttonTop.singleClick { stopScan() }
//                views.buttonTop.show()
//                views.buttonLeft.gone()
//                views.buttonRight.gone()
//            }
//
//            FileTreeManager.STATE.SCAN_STOPPING -> {
//                views.textViewState.setText(R.string.state_scan_stopping)
//                views.buttonTop.gone()
//                views.buttonLeft.gone()
//                views.buttonRight.gone()
//            }
//
//            FileTreeManager.STATE.SCAN_FINISH -> {
//                views.textViewState.setText(R.string.state_scan_finish)
//                views.buttonTop.setText(R.string.caption_button_rescan)
//                views.buttonLeft.setText(R.string.caption_button_startClean)
//                views.buttonRight.setText(R.string.caption_button_viewFiles)
//                views.buttonTop.singleClick { startScan() }
//                views.buttonLeft.singleClick { startClean() }
//                views.buttonRight.singleClick { viewFile() }
//                views.buttonTop.show()
//                views.buttonLeft.show()
//                views.buttonRight.show()
//            }
//
//            FileTreeManager.STATE.CLEAN_EXECUTING -> {
//                views.textViewState.setText(R.string.state_clean_executing)
//                views.buttonTop.setText(R.string.caption_button_stopClean)
//                views.buttonTop.singleClick { stopClean() }
//                views.buttonTop.show()
//                views.buttonLeft.gone()
//                views.buttonRight.gone()
//            }
//
//            FileTreeManager.STATE.CLEAN_STOPPING -> {
//                views.textViewState.setText(R.string.state_clean_stopping)
//                views.buttonTop.gone()
//                views.buttonLeft.gone()
//                views.buttonRight.gone()
//            }
//
//            FileTreeManager.STATE.CLEAN_FINISH -> {
//                views.textViewState.setText(R.string.state_clean_finish)
//                views.buttonTop.setText(R.string.caption_button_rescan)
//                views.buttonLeft.setText(R.string.caption_button_startClean)
//                views.buttonRight.setText(R.string.caption_button_viewFiles)
//                views.buttonTop.singleClick { startScan() }
//                views.buttonLeft.singleClick { startClean() }
//                views.buttonRight.singleClick { viewFile() }
//                views.buttonTop.show()
//                views.buttonLeft.show()
//                views.buttonRight.show()
//            }
//        }