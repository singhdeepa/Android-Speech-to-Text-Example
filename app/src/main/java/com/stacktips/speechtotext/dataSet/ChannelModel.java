package com.stacktips.speechtotext.dataSet;

public class ChannelModel {
   private String channelName,channelNumber,currentProgram,programActor,programActress;

    public ChannelModel() {
        // Default constructor required for calls to DataSnapshot.getValue(ChannelModel.class)
    }

    public ChannelModel(String channelName, String channelNumber, String currentProgram, String programActor, String programActress) {
        this.channelName=channelName;
        this.channelNumber=channelNumber;
        this.currentProgram=currentProgram;
        this.programActor=programActor;
        this.programActress=programActress;

    }
    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getChannelNumber() {
        return channelNumber;
    }

    public void setChannelNumber(String channelNumber) {
        this.channelNumber = channelNumber;
    }

    public String getCurrentProgram() {
        return currentProgram;
    }

    public void setCurrentProgram(String currentProgram) {
        this.currentProgram = currentProgram;
    }

    public String getProgramActor() {
        return programActor;
    }

    public void setProgramActor(String programActor) {
        this.programActor = programActor;
    }

    public String getProgramActress() {
        return programActress;
    }

    public void setProgramActress(String programActress) {
        this.programActress = programActress;
    }
}
