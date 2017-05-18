import org.encog.Encog;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.basic.BasicMLDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.arrayutil.NormalizeArray;

import java.io.*;

import static org.encog.persist.EncogDirectoryPersistence.loadObject;
import static org.encog.persist.EncogDirectoryPersistence.saveObject;

/**
 * Created by Alex on 19.04.2017.
 */
public class NeuronNetwork {

    private static NeuronNetwork instance;
    private BasicNetwork network;
    private double input[][];
    private double output[][];

    private NeuronNetwork(){}

    public static synchronized NeuronNetwork getInstance(){
        if (instance==null){
            instance = new NeuronNetwork();
        }
        return instance;
    }


    public double[][] getOutput() {
        return output;
    }

    public void setInput(double[][] input) {
        this.input = input;
    }

    public void loadTestFromFiles(){
        String folderName = new String("learn\\");
        File folder = new File(folderName);
        File files[] = folder.listFiles();
        input = new double[files.length][218];
        output = new double[files.length][2];
        //double buffer[] = new double[218];
        for (int i = 0; i < files.length; i++) {
            try {
                System.out.println("File "+ files[i].getName());
                BufferedReader in  = new BufferedReader(new FileReader(files[i]));
                double RMS=0;
                double maxDRMS=0;
                double averageDRMS=0;
                double RMSarray[] = new double[200];
                double MFCCarray[] = new double[15];
                RMS = Double.parseDouble(in.readLine());
                averageDRMS = Double.parseDouble(in.readLine());
                maxDRMS = Double.parseDouble(in.readLine());
                //bpm = Integer.parseInt(in.readLine());
                String temp = in.readLine();
                String RMSStringArray[] = temp.split(" ");
                temp = in.readLine();
                String MFCCStringArray[] = temp.split(" ");
                for (int k = 0; k < RMSStringArray.length; k ++) {
                    RMSarray[k]=Double.parseDouble(RMSStringArray[k]);
                    //buffer[k+200]=Double.parseDouble(DRMSStringArray[k]);
                }
                for (int k = 0; k < MFCCStringArray.length; k++) {
                    MFCCarray[k]=Double.parseDouble(MFCCStringArray[k]);
                }
                temp = in.readLine();
                if (temp!=null){
                    System.out.println(temp);
                    String ideal[] = temp.split(" ");
                    output[i][0] = Double.parseDouble(ideal[0]);
                    output[i][1] = Double.parseDouble(ideal[1]);
                } else {
                    output = new double[files.length][2];
                }
                NormalizeArray normRMS = new NormalizeArray();
                normRMS.setNormalizedHigh(1);
                normRMS.setNormalizedLow(-1);
                RMSarray = normRMS.process(RMSarray);

                NormalizeArray normMFCC = new NormalizeArray();
                normRMS.setNormalizedHigh(1);
                normRMS.setNormalizedLow(-1);
                MFCCarray = normRMS.process(MFCCarray);

                input[i][215] = RMS/32655;
                input[i][216] = averageDRMS/32655;
                input[i][217] = maxDRMS/32655;
                //buffer[218] = bpm;

                for (int j = 0; j < 200; j++) {
                    input[i][j] = RMSarray[j];
                }
                for (int j = 200; j < 215; j++) {
                    input[i][j] = MFCCarray[j-200];
                }

                System.out.println("Success");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public double[] normalizeInput(double[] RMS, double[] MFCC, double averageRMS, double averageDeltaRMS, double maxDelataRMS){
        double result[] = new double[218];
        NormalizeArray normRMS = new NormalizeArray();
        normRMS.setNormalizedHigh(1);
        normRMS.setNormalizedLow(-1);
        RMS = normRMS.process(RMS);

        NormalizeArray normMFCC = new NormalizeArray();
        normMFCC.setNormalizedHigh(1);
        normMFCC.setNormalizedLow(-1);
        MFCC = normRMS.process(MFCC);

        result[215] = averageRMS/32655;
        result[216] = averageDeltaRMS/32655;
        result[217] = maxDelataRMS/32655;
        //buffer[218] = bpm;

        for (int j = 0; j < 200; j++) {
            result[j] = RMS[j];
        }
        for (int j = 200; j < 215; j++) {
            result[j] = MFCC[j-200];
        }
        return result;
    }

    public void saveNetwork(){
        saveObject(new File("network.eg"),this.network);
    }

    public void loadNetwork(){
        this.network = (BasicNetwork) loadObject(new File("network.eg"));
    }

    public void createNetwork(){
        network = new BasicNetwork();
        network.addLayer(new BasicLayer(null,true,217));
        network.addLayer(new BasicLayer(new ActivationTANH(),false,217));
        network.addLayer(new BasicLayer(new ActivationTANH(),false,20));
        network.addLayer(new BasicLayer(new ActivationTANH(),true,2));
        network.getStructure().finalizeStructure();
        network.reset();
    }

    public void learning(){
        this.loadTestFromFiles();

        MLDataSet trainingSet = new BasicMLDataSet(input, output);
        final ResilientPropagation train = new ResilientPropagation(network, trainingSet);

        int epoch = 1;
        do {
            train.iteration();
            System.out.println("Epoch #" + epoch + " Error:" + train.getError());
            epoch++;
        } while((train.getError() > 0.01)&&(epoch<2000));
        train.finishTraining();

        double tempResult[] = new double[2];
        for (int i = 0; i < input.length; i++) {
            network.compute(input[i],tempResult);
            System.out.println("Ideal: "+output[i][0]+" "+output[i][1]+"  Real: "+tempResult[0]+" "+tempResult[1]);
        }

    }

    public double[] calculate(double input[]){
        double output[] = new double[2];
        System.out.println(input);
        System.out.println(output);
        System.out.println(network);
        network.compute(input,output);
        return output;
    }

    public void shutdown(){
        Encog.getInstance().shutdown();
    }
}
