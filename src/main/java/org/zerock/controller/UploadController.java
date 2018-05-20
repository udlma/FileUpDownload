package org.zerock.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.zerock.util.MediaUtils;
import org.zerock.util.UploadFileUtils;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class UploadController {

	@GetMapping("/upload")
	public void upload() {

	}

	@PostMapping("/upload")
	public void uploadPost(MultipartFile file, Model model) throws Exception {

		log.info(file.getOriginalFilename());
		log.info(file.getSize());
		log.info(file.getContentType());

		UUID uid = UUID.randomUUID();

		String savedName = uid.toString() + "_" + file.getOriginalFilename();

		File target = new File("C:\\zzz\\upload", savedName);

		FileCopyUtils.copy(file.getBytes(), target);

		model.addAttribute("savedName", savedName);
	}

	@GetMapping("/ajaxupload")
	public void ajaxupload() {

	}

	@PostMapping(value = "/ajaxupload", produces = "text/plain;charset=UTF-8")
	public @ResponseBody ResponseEntity<String> uploadAjax(MultipartFile file) throws Exception {

		log.info(file.getOriginalFilename());
		log.info(file.getSize());
		log.info(file.getContentType());

		return new ResponseEntity<String>(
				UploadFileUtils.uploadFile("C:\\zzz\\upload", file.getOriginalFilename(), file.getBytes()),
				HttpStatus.CREATED);
	}

	@GetMapping("/displayFile")
	public @ResponseBody ResponseEntity<byte[]> displyFile(String fileName) throws Exception {

		InputStream in = null;
		ResponseEntity<byte[]> entity = null;

		log.info("file name : " + fileName);

		try {
			String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

			MediaType mType = MediaUtils.getMediaType(formatName);

			HttpHeaders headers = new HttpHeaders();

			in = new FileInputStream("C:\\zzz\\upload" + fileName);

			if (mType != null) {
				headers.setContentType(mType);
			} else {
				fileName = fileName.substring(fileName.indexOf("_"));
				headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
				headers.add("Content-Disposition",
						"attachment; filename=\"" + new String(fileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
			}

			entity = new ResponseEntity<byte[]>(IOUtils.toByteArray(in), headers, HttpStatus.CREATED);

		} catch (Exception e) {

			e.printStackTrace();
			entity = new ResponseEntity<byte[]>(HttpStatus.BAD_REQUEST);

		} finally {
			in.close();
		}

		return entity;
	}
	
	@PostMapping("/deleteFile")
	public @ResponseBody ResponseEntity<String> deleteFile(String fileName) throws Exception{
		
		String formatName = fileName.substring(fileName.lastIndexOf(".")+1);
		
		MediaType mType = MediaUtils.getMediaType(formatName);
		
		if(mType != null) {
			String front = fileName.substring(0, 12);
			String end = fileName.substring(14);
			new File("C:\\zzz\\upload" + (front+end).replace('/', File.separatorChar)).delete();
		}
		
		new File("C:\\zzz\\upload" + fileName.replace('/', File.separatorChar)).delete();
		
		log.info("============================================================");
		log.info("C:\\zzz\\upload" + fileName.replace('/', File.separatorChar));
		
		return new ResponseEntity<String>("deleted", HttpStatus.OK);
		
	}
	
}
