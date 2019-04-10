package de.mhus.cherry.reactive.model.ui;

public class IEngineClassLoader {

    private static IEngineClassLoader instance;

    public static synchronized IEngineClassLoader instance() {
        if (instance == null)
            instance = new IEngineClassLoader();
        return instance;
    }
    
    public static void setInstance(IEngineClassLoader instance) {
        IEngineClassLoader.instance = instance;
    }

    public Class<?> load(String name) throws ClassNotFoundException {
        return this.getClass().getClassLoader().loadClass(name);
    }

}
