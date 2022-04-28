package com.campus.myapp.controller;




import java.io.File;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.ibatis.javassist.bytecode.Descriptor.Iterator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.campus.myapp.service.FoodService;
import com.campus.myapp.vo.FoodVO;

@Controller
public class FoodController {
	
	@Inject
	FoodService service;
	
	@GetMapping("/food/main_food")
	public ModelAndView foodPage() {
		
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("food/main_food");		
		return mav;
		
	}
	
	@RequestMapping(value="/getFoodRecommend", method=RequestMethod.GET)
	@ResponseBody
	public List<FoodVO> foodRecommend(String weather, String temp){

		System.out.println(weather);
		System.out.println(temp);
		//���� ��õ
		//�켱 ���� �ִ� ���Ŀ��� 2��
		
		//���� ��õ ȭ������ ���� ���� ����Ʈ
		List<FoodVO> list = new ArrayList<FoodVO>();
		
		//���� ��ġ, ���� ��ġ, �µ� �ش� �ϴ� ���� ������ ����Ʈ
		HashSet<FoodVO> foods = new HashSet<FoodVO>();
		
		
		int cnt=0;
		//cnt<=2���� �϶�
		//1. ���� ��¥�� ��ġ�ϴ� �̺�Ʈ ��¥ �ִ��� Ȯ�� -- 0��, 1��, 2�� �̻�...(������ �ִٸ� ���� 1�� �����ؼ� ��� ����Ʈ�� �߰�)
		//���� ��¥
		LocalDate now = LocalDate.now();
		System.out.println(now.toString());
		//���� ��¥�� ��ġ�ϴ� �̺�Ʈ �ִ� ���� ����
		List<FoodVO> event = service.getEqualEvent(now.toString());
		
		
		if(event.size()>=2) {
			//1�� �����ϱ�
			Collections.shuffle(event);
			list.add(event.get(0));
			cnt=1;
		}else if(event.size()==1) {
			//1��
			list.add(event.get(0));
			cnt=1;
		}
		//0���� ��� - cnt = 0 �Ѿ��
		
		
		//2. ���� ������ ��ġ�ϴ� ���� �ִ��� Ȯ��
		//���� ����
		//System.out.println(weather);
		
		String todayWeather = "";
		
		if(weather.contains("����")) {
			todayWeather = "clear";
			
		}else if(weather.contains("��") || weather.contains("�ҳ���")) {
			
			todayWeather = "rain";
			
		}else if(weather.contains("��")) {
			todayWeather = "snow";
			
		}
		if(!todayWeather.equals("")) {
			foods.addAll(service.getEqualWeather(todayWeather));
		}
		
		
		//3. ���� ������ ��ġ�ϴ� ���� �ִ��� Ȯ��
		int month = now.getMonthValue();
		
		String season = "";
		
		if(month>=3 && month <=5) {
			season = "spring";
		}else if(month>=6 && month <=8) {
			season = "summer";
		}else if(month>=9 && month <=11) {
			season = "fall";
		}else if(month <= 2 || month ==12) {
			season = "winter";
		}
		
		if(!season.equals("")) {
			foods.addAll(service.getEqualSeason(season));		
		}
		
		
		//4. ���� �µ��� �ش��ϴ� ���� �ִ��� Ȯ��
		Double tem = Double.parseDouble(temp);
		int temperature = 0;
		
		if(tem <=15) {
			temperature = 2;
		}else if(tem>=25) {
			temperature= 1;
		}
		
		if(temperature >0) {
			foods.addAll(service.getEqualTemp(temperature));
		}
		
		
		
		//event���� ���õ� �Ͱ� �ߺ��Ǵ� ������ ����
		if(list.size()>0) {
			foods.removeIf(FoodVO->FoodVO.getFname().equals(list.get(0).getFname()));
		}
		
		// 2,3,4�߰��� ��ü �ߺ� ����
		//fname���� ��ü ���� 
		
		for(FoodVO fvo: foods) {
			System.out.println(fvo.getFname());
		}
		
		List<FoodVO> f = new ArrayList<FoodVO>(foods);
		
		//2,3,4, ��� ����Ʈ�� �߰� <- (1���� 0���̸� 2�� ����, 1�� �̻��̸� 1�� ����)
		
		
		if(f.size()>0) {
			
			Collections.shuffle(f);
			list.add(f.get(0));
			cnt++;
			
			if(cnt==1 && f.size()>1) {
				list.add(f.get(1));
				cnt++;
			}
		}
		
		for(FoodVO fvo: list) {
			System.out.println(fvo.getFname()+ "+");
		}
			
		
		//�켱 ���� ���� ���� ��������
		//priorty==N�� ���� ����Ʈ �������� ���� ������ 3�� �Ǵ� 4�� �Ǵ� 5��
		List<FoodVO> priorityN = service.getPriorityN("N");
		Collections.shuffle(priorityN);
			
		int i=0;
		while(cnt<5) {
			list.add(priorityN.get(i));
			i++;
			cnt++;
		}
		/*
			
		for(FoodVO fvo: list) {
			System.out.println(fvo.getFname());
		}
		*/
			
		return list;
		
	}
	
	@GetMapping("/master/master_food")
	public ModelAndView foodAdminPage() {
		
		ModelAndView mav = new ModelAndView();
		mav.setViewName("master/master_food");
		return mav;
	}
	
	//���� �߰�
	@PostMapping("/master/foodAdd")
	public ResponseEntity<String> foodAddOk(FoodVO vo, HttpServletRequest request){
		
		ResponseEntity<String> entity = null;
		
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8"))); 
		
		//�̹� �����ͺ��̽��� �ش� �̸��� ������ �ִ��� Ȯ��
		int result = service.checkFoodName(vo.getFname());
		
		if(result>0) {
			
			String msg = "<script>alert('�ش� ������ �̹� ����Ǿ� �ֽ��ϴ�.'); history.back(); </script>";
			
			entity = new ResponseEntity(msg, headers,HttpStatus.BAD_REQUEST );
			
		}else {		
			//���� ���ε带 ���� ���ε� ��ġ�� ���� �ּ�
			//String path = "/img/foodimg/upload";
			String path = request.getSession().getServletContext().getRealPath("/img/foodimg/upload");
			
			System.out.println(path);
			
			//event�� no�� �������ֱ�
			if((vo.getEvent()).equals("no")) {
				vo.setEvent(null);
			}
			
			if((vo.getWeather()).equals("allweather")) {
				vo.setWeather(null);
			}
			
			System.out.println(vo.getEvent()); 
			System.out.println(vo.getPriority());
			
			try {
				
				//���� ���ε带 ���� request ��ü���� multipart ��ü�� ����ȯ
				MultipartHttpServletRequest mr = (MultipartHttpServletRequest) request;
				
				MultipartFile file = mr.getFile("filename");
				
				String orgFileName = file.getOriginalFilename();
				System.out.println(orgFileName);
				
				/////////////////////////////////////////////////////
				//rename
				if(orgFileName != null && !orgFileName.equals("")) {
					
					File f = new File(path, orgFileName);
					
					//������ �����ϴ��� Ȯ��
					if(f.exists()) {
						
						for(int num=1; ; num++) {
							
							int point = orgFileName.lastIndexOf(".");
							
							String fileName = orgFileName.substring(0,point);
							
							String ext = orgFileName.substring(point+1);
							
							f = new File(path, fileName + "(" + num + ")."+ ext);
							
							if(!f.exists()) {
								orgFileName = f.getName();
								break;
									
							}
						}
						
					}//���� ���� Ȯ�� �Ϸ�
					
					//���� ���ε�
					try {
						file.transferTo(f);
						System.out.println("���� ���ε�");
						vo.setFoodimg(orgFileName);
						
					}catch(Exception ee) {
						
					}
					
				}
				
				
				//DB�� �߰�
				service.foodInsert(vo);
				
				String msg = "<script>alert('������ �߰��� �Ϸ�Ǿ����ϴ�.');location.href='/master/master_food'; </script>";
			
				entity = new ResponseEntity(msg, headers,HttpStatus.OK );
				
			}catch(Exception e) {
				e.printStackTrace();
				//���� ����
				deleteFile(path, vo.getFoodimg());
				
				String msg = "<script>alert('������ �߰��� �����Ͽ����ϴ�.'); history.back(); </script>";
				entity = new ResponseEntity(msg, headers,HttpStatus.BAD_REQUEST );
			}
		}
		return entity;
	}
	
	@PostMapping("/getFoodData")
	@ResponseBody
	public FoodVO sendFoodData(@RequestParam("searchFood") String searchFood) {
		
		return service.getFoodData(searchFood);
		
	}

	@PostMapping("/showfoods")
	@ResponseBody
	public List<FoodVO> showFoods(@RequestParam("foodType") String type) {
		
		List<FoodVO> foods = new ArrayList<FoodVO>();
		
		if(type.equals("��ü")) {
			foods = service.getAllFood();
		}else if(type.equals("��Ÿ")){
			foods = service.getEtcFood();
		}else {
			foods = service.getCategoryFood(type);
		}
		return foods;
	}
	
	
	 @PostMapping("/master/foodModify") 
	 public ResponseEntity<String> foodModifyOk(FoodVO vo, HttpServletRequest request){
		 
		 ResponseEntity<String> entity = null;
		 
		 HttpHeaders headers = new HttpHeaders();
		 
		 headers.setContentType(new MediaType("text", "html", Charset.forName("UTF-8")));
		 
		 System.out.println(vo.getFoodimg());
		 
		
		 if((vo.getEvent()).equals("no")) {
			 vo.setEvent(null);
		 }
		 if((vo.getWeather()).equals("allweather")) {
			 vo.setWeather(null);
		 }
		 
		 //DB���� ���� �̸� ��������
		 String priorFile = service.getFileName(vo.getFname());
		 System.out.println(priorFile+"<<<");
		 
		 //���� ���ε带 ���� ���ε� ��ġ�� ���� �ּ�
		 //String path = "/img/foodimg/upload";
		 String path = request.getSession().getServletContext().getRealPath("/img/foodimg/upload");
		 
			 
			 try {
				 //���� ���� �̹��� ���� �ø���
				 MultipartHttpServletRequest mr = (MultipartHttpServletRequest)request;
				 
				 MultipartFile newfile = mr.getFile("filename");
				 
				 
				 ////////////////////////////////////////////////////////////////////////
				 if(newfile != null) { //���� ���ε� �� ���� ���� ���
					 
					 String fileName = newfile.getOriginalFilename();
					 
					 if(fileName != null && !fileName.equals("")) {
						 
						 File f = new File(path, fileName);
						 
						 if(f.exists()) { //���� �̸��� ������ ���� ���
							 
							 for(int num=1;;num++) {
								 
								 int point = fileName.lastIndexOf(".");
								 
								 String fileNameExt = fileName.substring(0,point);
								 String ext = fileName.substring(point+1);
								 
								 f = new File(path, fileNameExt +"(" + num +")." + ext);
								 
								 if(!f.exists()) {
									 fileName = f.getName();
									 break;
									 
								 }
								 
							 }//for
						 }///if ���� �̸��� ����
						 
						///���ϳֱ�	 
						 try {
							 newfile.transferTo(f);
							 System.out.println("���� ���ε�");
							 
							 if(!newfile.isEmpty()) {
								 //÷���ߴٸ�
								 deleteFile(path, priorFile);
							 }
						 
							 vo.setFoodimg(fileName);
						 }catch(Exception ex) {}
						 
						 
					 }//if
					  
				 }//if
				 
				 
				
				 //DB ������Ʈ
				 
				 service.foodUpdate(vo);
				 
				 String msg = "<script>alert('���� ������ �Ϸ�Ǿ����ϴ�.');location.href='/master/master_food';</script>";
				 
				 entity = new ResponseEntity(msg, headers, HttpStatus.OK);
				 
				 
				 
			 }catch(Exception e) {
				 
				 e.printStackTrace();
				 
				 deleteFile(path, vo.getFoodimg());
				 
				 String msg = "<script>alert('���� ������ �����Ͽ����ϴ�.');history.back();</script>";
				 entity = new ResponseEntity(msg, headers, HttpStatus.BAD_REQUEST);
				 
			 }
		
		 return entity;

	 }
	
	//���� ����
	public void deleteFile(String p, String f) {
		
		if(f!=null) {
			
			File file = new File(p,f);
			file.delete();
		}
		
	}
}


