package mx.desarrollo.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Usuario.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Usuario_ {

	
	/**
	 * @see mx.desarrollo.entity.Usuario#contrasena
	 **/
	public static volatile SingularAttribute<Usuario, String> contrasena;
	
	/**
	 * @see mx.desarrollo.entity.Usuario
	 **/
	public static volatile EntityType<Usuario> class_;
	
	/**
	 * @see mx.desarrollo.entity.Usuario#nombre
	 **/
	public static volatile SingularAttribute<Usuario, String> nombre;
	
	/**
	 * @see mx.desarrollo.entity.Usuario#rfc
	 **/
	public static volatile SingularAttribute<Usuario, String> rfc;

	public static final String CONTRASENA = "contrasena";
	public static final String NOMBRE = "nombre";
	public static final String RFC = "rfc";

}

