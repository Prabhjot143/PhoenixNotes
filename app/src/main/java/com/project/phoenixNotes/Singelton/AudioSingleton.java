package com.project.phoenixNotes.Singelton;

public class AudioSingleton
{
    // static variable single_instance of type Singleton
    private static AudioSingleton single_instance = null;

    // variable of type String
    public String audioUrl;

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    // static method to create instance of Singleton class
    public static AudioSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new AudioSingleton();

        return single_instance;
    }
}
