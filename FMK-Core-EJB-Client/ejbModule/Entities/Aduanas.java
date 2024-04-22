package Entities;

import java.sql.Date;

import javax.persistence.Id;

// @Entity //2014 pru
public class Aduanas {
	@Id private String codadu;
	
	private String nombre;
	private String nombre_abr;
	private String titulo;
	private String nroctapesos;
	private String nroctadolares;
	private String administrador;
	private String domiciliio;
	private String localidad;
	private String provincia;
	private String codpostal;
	private String telefono;
	private Date desde;
	private Date hasta;
	private String email;
	private String jurisdiccion;
	
	public Aduanas() {
	}

	public String getCodadu() {
		return codadu;
	}

	public void setCodadu(String codadu) {
		this.codadu = codadu;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getNombre_abr() {
		return nombre_abr;
	}

	public void setNombre_abr(String nombre_abr) {
		this.nombre_abr = nombre_abr;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getNroctapesos() {
		return nroctapesos;
	}

	public void setNroctapesos(String nroctapesos) {
		this.nroctapesos = nroctapesos;
	}

	public String getNroctadolares() {
		return nroctadolares;
	}

	public void setNroctadolares(String nroctadolares) {
		this.nroctadolares = nroctadolares;
	}

	public String getAdministrador() {
		return administrador;
	}

	public void setAdministrador(String administrador) {
		this.administrador = administrador;
	}

	public String getDomiciliio() {
		return domiciliio;
	}

	public void setDomiciliio(String domiciliio) {
		this.domiciliio = domiciliio;
	}

	public String getLocalidad() {
		return localidad;
	}

	public void setLocalidad(String localidad) {
		this.localidad = localidad;
	}

	public String getProvincia() {
		return provincia;
	}

	public void setProvincia(String provincia) {
		this.provincia = provincia;
	}

	public String getCodpostal() {
		return codpostal;
	}

	public void setCodpostal(String codpostal) {
		this.codpostal = codpostal;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Date getDesde() {
		return desde;
	}

	public void setDesde(Date desde) {
		this.desde = desde;
	}

	public Date getHasta() {
		return hasta;
	}

	public void setHasta(Date hasta) {
		this.hasta = hasta;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getJurisdiccion() {
		return jurisdiccion;
	}

	public void setJurisdiccion(String jurisdiccion) {
		this.jurisdiccion = jurisdiccion;
	}

	
}
