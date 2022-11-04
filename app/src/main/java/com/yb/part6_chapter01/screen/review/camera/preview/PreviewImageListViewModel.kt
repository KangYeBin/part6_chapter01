package com.yb.part6_chapter01.screen.review.camera.preview

import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.yb.part6_chapter01.model.preview.PreviewImageModel
import com.yb.part6_chapter01.screen.base.BaseViewModel
import com.yb.part6_chapter01.util.path.PathUtil
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileNotFoundException

class PreviewImageListViewModel(
    private val uriList: List<Uri>,
) : BaseViewModel() {

    private val previewImageList: MutableList<Uri> = uriList.toMutableList()

    val previewImageListStateLiveData =
        MutableLiveData<PreviewImageListState>(PreviewImageListState.Uninitialized)

    override fun fetchData(): Job = viewModelScope.launch {
        previewImageListStateLiveData.value = PreviewImageListState.Loading
        previewImageListStateLiveData.value = PreviewImageListState.Success(previewImageList.map {
            PreviewImageModel(
                id = it.hashCode().toLong(),
                uri = it
            )
        })
    }

    fun removeImage(uri: Uri, context: Context) {
        val file = File(PathUtil.getPath(context, uri) ?: throw FileNotFoundException())
        file.delete()
        previewImageList.remove(uri)
        MediaScannerConnection.scanFile(context, arrayOf(file.path), arrayOf(file.name), null)
        fetchData()
    }

    fun confirmImage() {
        when (val data = previewImageListStateLiveData.value) {
            is PreviewImageListState.Success -> {
                previewImageListStateLiveData.value = PreviewImageListState.Confirm(
                    data.previewImageList
                )
            }
            else -> Unit
        }
    }
}
