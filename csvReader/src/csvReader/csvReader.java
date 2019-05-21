package csvReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

import com.espertech.esper.common.client.EPCompiled;
import com.espertech.esper.common.client.configuration.Configuration;
import com.espertech.esper.compiler.client.CompilerArguments;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.compiler.client.EPCompiler;
import com.espertech.esper.compiler.client.EPCompilerProvider;
import com.espertech.esper.runtime.client.EPDeployException;
import com.espertech.esper.runtime.client.EPDeployment;
import com.espertech.esper.runtime.client.EPRuntime;
import com.espertech.esper.runtime.client.EPRuntimeProvider;
import com.espertech.esper.runtime.client.EPStatement;

public class csvReader {
	static PrintWriter out = null;

	public static void main(String[] args) throws IOException {
		ServerSocket serverSocket= null;
		Socket socket =null;
		BufferedReader datainputstream=null;


		//Subindo o servidor para receber o onibus selecionado
		serverSocket = new ServerSocket(8888);
		System.out.println("Listening ...");
		socket = serverSocket.accept();

		datainputstream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(socket.getOutputStream(), true);

		String linha = null;
		linha = datainputstream.readLine();
		int valorOnibus = Integer.parseInt(linha);

		String csvFile = "C:\\Users\\gusta\\Downloads\\200118_240118.csv";
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";

		EPCompiler compiler = EPCompilerProvider.getCompiler();
		Configuration configuration = new Configuration();
		configuration.getCommon().addEventType(BusEvent.class);

		CompilerArguments cargs = new CompilerArguments(configuration);
		EPCompiled epCompiled;
		String consulta = "@name('get-in') select instante, latitude, longitude from BusEvent where matriculaBus = '" + valorOnibus + "' order by instante";  
		try {
			epCompiled = compiler.compile(consulta, cargs);

		} catch (EPCompileException ex) {
			throw new RuntimeException(ex);
		}
		EPRuntime runtime = EPRuntimeProvider.getDefaultRuntime(configuration);
		EPDeployment deployment;
		try {
			deployment = runtime.getDeploymentService().deploy(epCompiled);
		} catch (EPDeployException ex) {
			throw new RuntimeException(ex);
		}

		EPStatement statement = runtime.getDeploymentService().getStatement(deployment.getDeploymentId(), "get-in");

		statement.addListener((newData, oldData, mystatement, myruntime) -> {
			String latitude = (String) newData[0].get("latitude");
			String longitude = (String) newData[0].get("longitude");
			String instante = (String) newData[0].get("instante");
			System.out.println(instante + ": "+latitude + "," + longitude);
			try {
				TimeUnit.MILLISECONDS.sleep(1500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			out.println(latitude + " " + longitude);
		});

		try {

			br = new BufferedReader(new FileReader(csvFile));
			br.readLine();
			while ((line = br.readLine()) != null) {
				String[] csvArray = line.split(cvsSplitBy);
				csvArray[5] = "25 L " + csvArray[6] + " " + csvArray[7];

				CoordinateConversion latLong = new CoordinateConversion();
				double[] convertido = latLong.utm2LatLon(csvArray[5]);

				String unidade = csvArray[0];
				String nomeEmpresa = csvArray[1];
				String matriculaBus = csvArray[2];
				String instante = csvArray[3];
				String latitude =  Double.toString(convertido[0]);
				String longitude =  Double.toString(convertido[1]);

				BusEvent evento = new BusEvent();
				evento.setUnidade(unidade);
				evento.setNomeEmpresa(nomeEmpresa);
				evento.setMatriculaBus(matriculaBus);
				evento.setInstante(instante);
				evento.setLatitude(latitude);
				evento.setLongitude(longitude);

				runtime.getEventService().sendEventBean(evento, "BusEvent");
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
