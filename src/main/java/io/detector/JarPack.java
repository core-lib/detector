package io.detector;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

/**
 * extended jar file
 *
 * @author 杨昌沛 646742615@qq.com
 * @date 2018-08-29 10:38
 **/
public class JarPack extends JarFile {
    private final String jarName;
    private final JarFile jarFile;
    private final File tmpFile;

    public JarPack(String path) throws IOException {
        this(path, true);
    }

    public JarPack(String path, boolean verify) throws IOException {
        super(path.split("!")[0], verify);
        InputStream in = null;
        OutputStream out = null;
        try {
            String[] parts = path.split("!");
            if (parts.length == 1) {
                this.jarName = null;
                this.jarFile = null;
                this.tmpFile = null;
                return;
            }
            String url = "jar:file:" + (path.startsWith("/") ? "" : "/") + path;
            String name = path.substring(path.lastIndexOf("/") + 1);
            int index = name.lastIndexOf('.');
            String prefix = index < 0 ? name : name.substring(0, index);
            String suffix = index < 0 ? "" : name.substring(index);
            this.tmpFile = File.createTempFile(prefix + "(", ")" + suffix);
            in = new URL(url).openStream();
            out = new FileOutputStream(tmpFile);
            IoKit.transfer(in, out);
            this.jarName = (path.startsWith("/") ? "" : "/") + path;
            this.jarFile = new JarFile(tmpFile, verify);
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        } finally {
            IoKit.close(in);
            IoKit.close(out);
        }
    }

    public JarPack(File file) throws IOException {
        super(file);
        this.jarName = null;
        this.jarFile = null;
        this.tmpFile = null;
    }

    public JarPack(File file, boolean verify) throws IOException {
        super(file, verify);
        this.jarName = null;
        this.jarFile = null;
        this.tmpFile = null;
    }

    public JarPack(File file, boolean verify, int mode) throws IOException {
        super(file, verify, mode);
        this.jarName = null;
        this.jarFile = null;
        this.tmpFile = null;
    }

    @Override
    public Manifest getManifest() throws IOException {
        return jarFile.getManifest();
    }

    @Override
    public JarEntry getJarEntry(String name) {
        return jarFile == null ? super.getJarEntry(name) : jarFile.getJarEntry(name);
    }

    @Override
    public ZipEntry getEntry(String name) {
        return jarFile == null ? super.getEntry(name) : jarFile.getEntry(name);
    }

    @Override
    public Enumeration<JarEntry> entries() {
        return jarFile == null ? super.entries() : jarFile.entries();
    }

    @Override
    public InputStream getInputStream(ZipEntry ze) throws IOException {
        return jarFile == null ? super.getInputStream(ze) : jarFile.getInputStream(ze);
    }

    @Override
    public String getComment() {
        return jarFile == null ? super.getComment() : jarFile.getComment();
    }

    @Override
    public String getName() {
        return jarFile == null ? super.getName() : jarName;
    }

    @Override
    public int size() {
        return jarFile == null ? super.size() : jarFile.size();
    }

    @Override
    public void close() throws IOException {
        IoKit.close(jarFile);
        IoKit.delete(tmpFile);
        super.close();
    }

}
