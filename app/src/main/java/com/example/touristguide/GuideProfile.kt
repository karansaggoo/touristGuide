package com.example.touristguide

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.method.TextKeyListener.clear
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.*
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.fragment.app.Fragment
import com.example.touristguide.databinding.FragmentGuideProfileBinding
import com.example.touristguide.model.Guide
import com.example.touristguide.model.GuideRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class GuideProfile : Fragment() {
    private var _binding : FragmentGuideProfileBinding? = null
    private val binding get() = _binding!!
    lateinit var guideRepository: GuideRepository
    private final var RC_PHOTO_PICKER =2
    private var selectedImageUri:String?=null
    private lateinit var photoStorageRefeference:StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGuideProfileBinding.inflate(inflater, container, false)
        photoStorageRefeference  = FirebaseStorage.getInstance().getReference().child("profile_photos")

        guideRepository = GuideRepository(requireContext())
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.photoPicker.setOnClickListener {

                pickMedia.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))

        }

        binding.profileUpdate.setOnClickListener {
            if(selectedImageUri==null){
                guideRepository.addUserToDB(Guide(email = "harsh", name = "fcd", tel = 123456, desc = "vfdctvf", loc = "toronto", imageUri = ""))
            }
            else{
                var email = binding.guideEmail
                var name = binding.guideName
                var tel = binding.guideTel
                var desc= binding.guideDesc
                var loc = binding.guideLoc
                guideRepository.addUserToDB(Guide(email = "harsh", name = "fcd", tel = 123456, desc = "vfdctvf", loc = "toronto", imageUri = selectedImageUri!!))
            }
            }



        guideRepository.getGuideByEmail("harsh")
        guideRepository.guideList.observe(viewLifecycleOwner){
                list ->
            if(list != null){
                for(guide in list){
                    binding.guideName.setText(guide.name)
                }
            }
        }



    }

//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if(requestCode==RC_PHOTO_PICKER && resultCode== RESULT_OK){
//             selectedImageUri = data!!.data
//            if (selectedImageUri != null) {
//                binding.profilePic.setImageURI(selectedImageUri)
////                var photoRef = mChatPhotoStorageRefeference.child(selectedImageUri.lastPathSegment!!)
////                photoRef.putFile(selectedImageUri).addOnSuccessListener { taskSnapshot ->
////                    var downloadUrl = taskSnapshot.uploadSessionUri
////                    var friendlyMessage = FriendlyMessage(null,mUsername,downloadUrl.toString())
////                    mMessageDatabaseReference.push().setValue(friendlyMessage)
////
//            }
//        }
//    }
    var pickMedia = registerForActivityResult(
        PickVisualMedia()
    ) { uri: Uri? ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            binding.profilePic.setImageURI(uri)
            var photoRef = photoStorageRefeference.child(uri.lastPathSegment!!)
            photoRef.putFile(uri).addOnSuccessListener { taskSnapshot ->
                var downloadUrl = taskSnapshot.uploadSessionUri
                selectedImageUri = downloadUrl.toString()
                Log.d("PhotoPicker", "Selected URI: $uri")
            }
        }else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onStart() {
        super.onStart()
        //binding.guideName.setText(guideRepository.guide.name)
    }

    override fun onResume() {
        super.onResume()
        //binding.guideName.setText(guideRepository.guide.name)
    }





}


