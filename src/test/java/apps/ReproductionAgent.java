package apps;

import core.excelUserData;
import io.appium.java_client.windows.WindowsDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import tests.TestBase;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ReproductionAgent extends TestBase {

    WindowsDriver driverWinRA;
    List<Map<String,String>> pickDateToReproduceFromFile;

    public ReproductionAgent(WindowsDriver driverWinRA){this.driverWinRA = driverWinRA;}

    public String setJobRepAgent() throws Exception {
        System.out.println("Inside Reproduction Agent");
        pickDateToReproduceFromFile = excelUserData.getPolluxGWDataFromFile();
        Thread.sleep(1000);
        WebElement startDatePicker = driverWinRA.findElementByAccessibilityId("ReproductionWizardStartDatePicker");
        startDatePicker.click();
        //getElementCoordinates(startDatePicker);
        Actions a = new Actions(driverWinRA);
        System.out.println("......StartingDatePicker US Format........");
        a.moveToElement(startDatePicker, 10, 0).click().sendKeys(pickDateToReproduceFromFile.get(0).get("StartMonth")).build().perform();
        System.out.println("Changed the starting month....");
        a.moveToElement(startDatePicker, 40, 0).click().sendKeys(pickDateToReproduceFromFile.get(0).get("StartDay")).build().perform();
        System.out.println("Changed the starting day....");
        a.moveToElement(startDatePicker, 60, 0).click().sendKeys(pickDateToReproduceFromFile.get(0).get("StartYear")).build().perform();
        System.out.println("Changed the starting year....");
        WebElement endDatePicker = driverWinRA.findElementByAccessibilityId("ReproductionWizardEndDatePicker");
        System.out.println("......EndingDatePicker US Format........");
        endDatePicker.click();
        a.moveToElement(endDatePicker, 10, 0).click().sendKeys(pickDateToReproduceFromFile.get(0).get("EndMonth")).build().perform();
        System.out.println("Changed the starting month....");
        a.moveToElement(endDatePicker, 40, 0).click().sendKeys(pickDateToReproduceFromFile.get(0).get("EndDay")).build().perform();
        System.out.println("Changed the starting day....");
        a.moveToElement(endDatePicker, 60, 0).click().sendKeys(pickDateToReproduceFromFile.get(0).get("EndYear")).build().perform();
        System.out.println("Changed the ending year....");
        driverWinRA.findElementByAccessibilityId("ReproductionWizardPolluxGatewayProdTypeCheckBox").click();
        driverWinRA.findElementByAccessibilityId("TimeWindowCheckBox").click();
        driverWinRA.findElementByAccessibilityId("ReproductionWizardNextButton").click();
        String[] title = driverWinRA.getTitle().split(" ");
        takeAppSnap(driverWinRA,title[0]+"_START");
        driverWinRA.findElementByAccessibilityId("JobMonitorExecuteButton").click();
        Thread.sleep(3000);
        //checkIfProcessIsComplete();
        String endJobs = checkIfJobsProcessIsCompleted();
        String s = driverWinRA.findElementByAccessibilityId("JobMonitorCurrentJobProductionMonitorCounterLabel").getText();
        System.out.println(s);
        takeAppSnap(driverWinRA,title[0]+"_END");
        return endJobs;
    }

    private String checkIfJobsProcessIsCompleted() throws Exception {
        pickDateToReproduceFromFile = excelUserData.getPolluxGWDataFromFile();
        int s = 0;
        String jobs = driverWinRA.findElementByAccessibilityId("JobMonitorCurrentJobProductionMonitorCounterLabel").getText();
        String[] splitJobs = jobs.split(" ");
        int totalJobsRunning = Integer.parseInt(splitJobs[4]);
        System.out.println("@@@@@@ This operation might take a few minutes to complete. Please be patient @@@@@@");
        Thread.sleep(180000); //wait 2mins
        do {
            try{
                s = Objects.requireNonNull(new File("C:\\UNITAM\\PolluxGateway\\Panel_0\\Files\\FromPanelData\\PolluxFromPanelTPI\\" + pickDateToReproduceFromFile.get(0).get("StartYear").replace(".0", "") + "\\" + pickDateToReproduceFromFile.get(0).get("StartMonth").replace(".0", "") + "\\" + pickDateToReproduceFromFile.get(0).get("StartDay").replace(".0", "")).list()).length;
            } catch (Exception e) {
                e.printStackTrace();
                System.out.print("Please wait - ");
                Thread.sleep(30000);  // 30sec
            }
        }while(s!=totalJobsRunning);
        if(driverWinRA.findElementByName("Reproduction Wizard - Process Complete").isDisplayed()){
                    System.out.println("@@@@@@  Reproduction Agent Process COMPLETED  @@@@@@");
                }
        return driverWinRA.findElementByName("Reproduction Wizard - Process Complete").getText();
    }

    private void checkIfProcessIsComplete() {
        int partialJobs = -1;
        int totalJobs = 0;
        System.out.println("..... Reproduction Wizard Process is still running, please wait till it ends .....");
        do {
            String jobs = driverWinRA.findElementByAccessibilityId("JobMonitorCurrentJobProductionMonitorCounterLabel").getText();
            String[] splitJobs = jobs.split(" ");
            int partialJobsRunning = Integer.parseInt(splitJobs[2]);
            int totalJobsRunning = Integer.parseInt(splitJobs[4]);
            if(partialJobsRunning==totalJobsRunning){
                System.out.println("##### Reproduction Wizard Process Complete");
                break; }
            partialJobs=partialJobsRunning;
            totalJobs=totalJobsRunning;
        } while (partialJobs != totalJobs);
    }

    public String getTitleRA() {
        //System.out.println("Inside getTitleRA method");
        String[] title = driverWinRA.getTitle().split(" ");
        return title[0];
    }

    public int getNumberTotalTPIFilesFromMixerOutputFolder() throws IOException {
        pickDateToReproduceFromFile = excelUserData.getPolluxGWDataFromFile();
        int tpi = Objects.requireNonNull(new File("C:\\UNITAM\\FileMaster\\Files\\"+pickDateToReproduceFromFile.get(0).get("StartYear").replace(".0","")+"\\"+pickDateToReproduceFromFile.get(0).get("StartMonth").replace(".0","")+"\\"+pickDateToReproduceFromFile.get(0).get("StartDay").replace(".0","")+"\\MixerOutput").list()).length;
        //System.out.println("TPIs numbers --->>>"+tpi);
        return tpi;
    }


}
