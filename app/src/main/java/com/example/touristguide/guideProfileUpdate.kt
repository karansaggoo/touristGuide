package com.example.touristguide

import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.touristguide.databinding.FragmentGuideProfileBinding
import com.example.touristguide.databinding.FragmentGuideProfileUpdateBinding
import com.example.touristguide.model.Guide
import com.example.touristguide.model.GuideRepository
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


class guideProfileUpdate : Fragment() {
    private var _binding : FragmentGuideProfileUpdateBinding? = null
    private val binding get() = _binding!!
    lateinit var guideRepository: GuideRepository
    private final var RC_PHOTO_PICKER =2
    private var selectedImageUri:String?=null
    private lateinit var prefs: SharedPreferences
    private lateinit var photoStorageRefeference: StorageReference
    var email = ""
    var name = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentGuideProfileUpdateBinding.inflate(inflater, container, false)
        photoStorageRefeference  = FirebaseStorage.getInstance().getReference().child("profile_photos")
        guideRepository = GuideRepository(requireContext())
        val view = binding.root
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs=requireContext().getSharedPreferences("com.example.touristguide", AppCompatActivity.MODE_PRIVATE)
         email = prefs.getString("USER_EMAIL","").toString()
        name = prefs.getString("USER_NAME","").toString()
        binding.guideName.setText(name)
        binding.guideEmail.setText(email)


        binding.photoPicker.setOnClickListener {

            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))

        }

        binding.profileUpdate.setOnClickListener {
            var tel = binding.guideTel.text.toString()
            var desc= binding.guideDesc.text.toString()
            var loc = binding.guideLoc.text.toString()
            if(selectedImageUri==null){
                Log.e("user",loc)
                Log.e("user",desc)
                Log.e("user",tel)
                guideRepository.addUserToDB(Guide(email = email, name = name, tel = tel , desc = desc, loc = loc, imageUri = ""))
            }
            else{
                Log.e("user",loc)
                Log.e("user",desc)
                Log.e("user",tel)
                guideRepository.addUserToDB(Guide(email = email, name = name, tel = tel , desc = desc, loc = loc, imageUri = ""))

            }
        }
    }

    var pickMedia = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
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


}