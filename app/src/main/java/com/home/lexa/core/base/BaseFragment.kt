package com.home.lexa.core.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding

// VB đại diện cho ViewBinding của từng màn hình cụ thể
abstract class BaseFragment<VB : ViewBinding>(
    private val inflate: (LayoutInflater, ViewGroup?, Boolean) -> VB
) : Fragment() {

    // Biến private có thể null để an toàn
    private var _binding: VB? = null

    // Biến public không null để các Fragment con gọi (chỉ gọi giữa onCreateView và onDestroyView)
    protected val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Hàm ảo để các Fragment con tự định nghĩa logic UI
        setupViews()
        observeData()
    }

    abstract fun setupViews()
    abstract fun observeData()

    override fun onDestroyView() {
        super.onDestroyView()
        // CỰC KỲ QUAN TRỌNG: Giải phóng bộ nhớ khi Fragment bị đóng
        _binding = null
    }
}