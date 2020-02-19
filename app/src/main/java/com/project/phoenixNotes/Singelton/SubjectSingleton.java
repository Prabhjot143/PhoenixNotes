package com.project.phoenixNotes.Singelton;

public class SubjectSingleton
{
    // static variable single_instance of type Singleton
    private static SubjectSingleton single_instance = null;

    // variable of type String
    public String subjectName;

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    // static method to create instance of Singleton class
    public static SubjectSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new SubjectSingleton();

        return single_instance;
    }
}