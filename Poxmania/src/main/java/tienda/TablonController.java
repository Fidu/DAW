package tienda;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TablonController {

	@Autowired
	private ProductoRepository repository;
	private static final String FILES_FOLDER = "files";
	
	@RequestMapping("/")
	public ModelAndView tablon(HttpSession sesion) {

		ModelAndView mv = new ModelAndView("tablon").addObject("productos",
				repository.findAll());

		if (sesion.isNew()) {
			mv.addObject("saludo", "Empezamos xD");
		}

		return mv;
	}

	@RequestMapping(value = "/insertar", method = RequestMethod.POST)
	public ModelAndView insertar(@RequestParam("file") MultipartFile file, Product producto, HttpSession sesion) {
		
		String fileName = repository.count() + ".jpg";
		if (!file.isEmpty()) {
			try {

				File filesFolder = new File(FILES_FOLDER);
				if (!filesFolder.exists()) {
					filesFolder.mkdirs();
				}

				File uploadedFile = new File(filesFolder.getAbsolutePath(), fileName);
				file.transferTo(uploadedFile);
				
				producto.setImageName(uploadedFile.getName());
			} catch (Exception e) {
				return new ModelAndView("tablon").addObject("productos",
						repository.findAll());
			}
		} else {
			return new ModelAndView("tablon").addObject("productos",
					repository.findAll());
		}
		
		
		repository.save(producto);
		
		return new ModelAndView("insertar").addObject("name",producto.getName());
	}

	@RequestMapping("/mostrar")
	public ModelAndView mostrar(@RequestParam long idProducto) {

		Product product = repository.findOne(idProducto);

		return new ModelAndView("mostrar").addObject("producto", product);
	}
	
	@RequestMapping("/nuevoProducto")
	public ModelAndView nuevoProducto(HttpSession sesion) {

		String name = (String) sesion.getAttribute("name");
		
		return new ModelAndView("nuevoProducto").addObject("name", name);
	}
	
	//Modulo para poder mostrar imágenes. Sin este mapping el servidor no sabrá "leer una img"
	@RequestMapping("/image/{fileName}")
	public void handleFileDownload(@PathVariable String fileName,
			HttpServletResponse res) throws FileNotFoundException, IOException {

		File file = new File(FILES_FOLDER, fileName+".jpg");

		if (file.exists()) {
			res.setContentType("image/jpeg");
			res.setContentLength(new Long(file.length()).intValue());
			FileCopyUtils
					.copy(new FileInputStream(file), res.getOutputStream());
		} else {
			res.sendError(404, "File" + fileName + "(" + file.getAbsolutePath()
					+ ") does not exist");
		}
	}

}