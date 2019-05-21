package csvReader;

public class BusEvent {
	private String unidade;
	private String nomeEmpresa;
	private String matriculaBus;
	private String instante;
	private String latitude;
	private String longitude;
	
	public String getUnidade() {
		return unidade;
	}
	
	public String getNomeEmpresa() {
		return nomeEmpresa;
	}
	
	public String getMatriculaBus() {
		return matriculaBus;
	}
	
	public String getInstante() {
		return instante;
	}
	
	public String getLatitude() {
		return latitude;
	}
	
	public String getLongitude() {
		return longitude;
	}
	
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	
	public void setNomeEmpresa(String nomeEmpresa) {
		this.nomeEmpresa = nomeEmpresa;
	}
	
	public void setMatriculaBus(String matriculaBus) {
		this.matriculaBus = matriculaBus;
	}
	
	public void setInstante(String instante) {
		this.instante = instante;
	}
	
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
}
