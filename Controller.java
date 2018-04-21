public class Controller {
	XTSAES xts;
	
	public Controller(String source, String key, String target){
		this.xts = new XTSAES(source, key, target);
	}
	
	public void encrypt() throws Exception{
		xts.encrypt();
	}
	
	public void decrypt() throws Exception{
		xts.decrypt();
	}


}
