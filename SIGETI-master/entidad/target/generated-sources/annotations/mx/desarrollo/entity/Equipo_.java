package mx.desarrollo.entity;

import jakarta.annotation.Generated;
import jakarta.persistence.metamodel.EntityType;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Equipo.class)
@Generated("org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
public abstract class Equipo_ {

	
	/**
	 * @see mx.desarrollo.entity.Equipo#marca
	 **/
	public static volatile SingularAttribute<Equipo, String> marca;
	
	/**
	 * @see mx.desarrollo.entity.Equipo#idEquipo
	 **/
	public static volatile SingularAttribute<Equipo, String> idEquipo;
	
	/**
	 * @see mx.desarrollo.entity.Equipo
	 **/
	public static volatile EntityType<Equipo> class_;
	
	/**
	 * @see mx.desarrollo.entity.Equipo#modelo
	 **/
	public static volatile SingularAttribute<Equipo, String> modelo;
	
	/**
	 * @see mx.desarrollo.entity.Equipo#idUsuarioAsignado
	 **/
	public static volatile SingularAttribute<Equipo, String> idUsuarioAsignado;
	
	/**
	 * @see mx.desarrollo.entity.Equipo#areaAsignada
	 **/
	public static volatile SingularAttribute<Equipo, String> areaAsignada;
	
	/**
	 * @see mx.desarrollo.entity.Equipo#estadoHardware
	 **/
	public static volatile SingularAttribute<Equipo, String> estadoHardware;

	public static final String MARCA = "marca";
	public static final String ID_EQUIPO = "idEquipo";
	public static final String MODELO = "modelo";
	public static final String ID_USUARIO_ASIGNADO = "idUsuarioAsignado";
	public static final String AREA_ASIGNADA = "areaAsignada";
	public static final String ESTADO_HARDWARE = "estadoHardware";

}

