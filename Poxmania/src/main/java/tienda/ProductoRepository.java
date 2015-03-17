package tienda;

import org.springframework.data.repository.CrudRepository;

public interface ProductoRepository extends CrudRepository<Product, Long> {

	//Aquí van las consultas a la BBDD
	
}