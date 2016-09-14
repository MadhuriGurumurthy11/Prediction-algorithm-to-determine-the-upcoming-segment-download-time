import java.io.*;
import java.net.*;
/**
*@author Madhuri Gurumurthy
*
*/
class ACN5Client {

	private final static String serverIP = "192.168.0.2";
	private final static int serverPort = 9997;
	private final static String fileOutput = "OneLove.wma";

	public static void main( String args[] ) {

		int runs = 1000;
		byte[] aByte = new byte[1];
		int bytesRead;
		int count = 0;
		Socket clientSocket = null;
		InputStream is = null;
		float[] timeTaken = new float[runs];
		float[] eachDownloadRate = new float[runs];
		float[] predictionTimeForNextRun = new float[runs];
		float[] valuesCompared = new float[runs];
		float startTime = 0, endTime = 0;
		for ( int i = 0; i < runs; i++ ) {
			try {
				// connect through socket
				clientSocket = new Socket( serverIP, serverPort );

				is = clientSocket.getInputStream( );
			} catch ( IOException ex ) {
				ex.printStackTrace( );
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream( );
			startTime = System.nanoTime( );
			if ( is != null ) {

				FileOutputStream fos = null;
				BufferedOutputStream bos = null;
				try {
					fos = new FileOutputStream( fileOutput );
					bos = new BufferedOutputStream( fos );
					bytesRead = is.read( aByte, 0, aByte.length );

					do {
						baos.write( aByte );
						bytesRead = is.read( aByte );
					} while ( bytesRead != -1 );

					bos.write( baos.toByteArray( ) );
					endTime = System.nanoTime( );
					bos.flush( );
					bos.close( );
					clientSocket.close( );
				} catch ( IOException ex ) {
					ex.printStackTrace( );
				}
			}

			// Time in seconds
			timeTaken[i] = ( endTime - startTime ) / 1000000000;

			try {
				Thread.sleep( 1500 );
			} catch ( InterruptedException e ) {
				e.printStackTrace( );
			}

			File file = new File( fileOutput );
			float fileSize = file.length( );// fileSize in bytes

			eachDownloadRate[i] = ( fileSize / timeTaken[i] );

			valuesCompared[i] = eachDownloadRate[i]
					- predictionTimeForNextRun[i];

			// Prediction Algorithm
			if ( i < 12 ) {
				predictionTimeForNextRun[i] = eachDownloadRate[i];
			} else if ( ( i + 1 ) < runs ) {
				predictionTimeForNextRun[i + 1] = ( fileSize )
						/ ( ( timeTaken[i] + timeTaken[i - 1]
								+ timeTaken[i - 2] + timeTaken[i - 3] + timeTaken[i - 4] ) / 5 );

			}
			System.out.println( "Predicted rate= "
					+ predictionTimeForNextRun[i] + " ------ Actual rate= "
					+ eachDownloadRate[i] );
		}

		// To count how many actual values are >= predicted values
		for ( int i = 0; i < runs; i++ ) {
			if ( valuesCompared[i] >= 0 )
				count++;
		}
		System.out.println( "Percentage of success= "
				+ ( ( count / runs ) * 100 ) );

	}
}
