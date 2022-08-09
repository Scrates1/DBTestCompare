package uk.co.objectivity.test.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.testng.TestException;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import uk.co.objectivity.test.db.beans.xml.CmpSqlResultsConfig;
import uk.co.objectivity.test.db.beans.xml.Replace;

public class GenerateXMLFromTemplate {
    private final static Logger log = Logger.getLogger(GenerateXMLFromTemplate.class);

    private static CmpSqlResultsConfig CMP_SQL_RESULTS_CONFIG;
    private static File TEMPLATES_DIR_FILE;
    private static boolean INITIALIZED = false;
    private static final String TEMPLATE_SUFFIX = ".ftl";

    static void init(File templateDirFile, CmpSqlResultsConfig cmpSqlResultsConfig) {
        log.debug("Initializing generate xmls from templates...");
        CMP_SQL_RESULTS_CONFIG = cmpSqlResultsConfig;
        TEMPLATES_DIR_FILE = templateDirFile;
        INITIALIZED = true;
    }

    public static void replaceCharactor() throws IOException, TemplateException {
        if (!INITIALIZED && RunTests.readConfigAndInit() == null) {
            throw new TestException("Can not read templates configuration");
        }
        if (!CMP_SQL_RESULTS_CONFIG.getTemplate().isReplaceOn()) {
            return;
        }
        log.debug("Reading templates configuration...");

        Map dataModel = getDataModel();
        if (dataModel == null || dataModel.isEmpty()) {
            log.debug("no valid replace in configuration, cancal replace templates...");
            return;
        }
        processTemplateFiles(TEMPLATES_DIR_FILE, TEMPLATES_DIR_FILE.getName(), dataModel);
    }

    private static Map getDataModel() {
        List<Replace> replaces = CMP_SQL_RESULTS_CONFIG.getTemplate().getReplaces();
        if (replaces == null) {
            return null;
        }
        Map dataModel = new HashMap();
        for (Replace replace : replaces) {
            dataModel.put(replace.getName(), replace.getValue());
            log.debug("the replace name:" + replace.getName() + ";the replace value:" + replace.getValue());
        }
        return dataModel;
    }

    private static void processTemplateFiles(File startDir, String templateNamePrefix, Map dataModel)
            throws IOException, TemplateException {
        Configuration conf = getConfiguration(startDir);

        for (File file : startDir.listFiles()) {

            if (file.isDirectory()) {
                String templateName = templateNamePrefix + "." + file.getName();
                processTemplateFiles(file, templateName, dataModel);
            } else if (file.getName().toLowerCase().endsWith(TEMPLATE_SUFFIX)) {
                generateFile(file, conf, dataModel);
            }
        }

    }

    private static Configuration getConfiguration(File file) throws IOException {
        // 第一步：创建一个Configuration对象，直接new一个对象。构造方法的参数就是freemarker对于的版本号。
        Configuration configuration = new Configuration(Configuration.getVersion());
        // 第二步：设置模板文件所在的路径。
        configuration.setDirectoryForTemplateLoading(file);
        // 第三步：设置模板文件使用的字符集。一般就是utf-8.
        configuration.setDefaultEncoding("utf-8");
        return configuration;
    }

    private static void generateFile(File file, Configuration configuration, Map dataModel)
            throws IOException, TemplateException {

        log.debug("template found: " + file.getAbsolutePath());
        // 第四步：加载一个模板，创建一个模板对象。

        Template template = configuration.getTemplate(file.getName());

        String xmlDirPath = getxmlDirPath(file);
        String xmlName = xmlDirPath + "/" + file.getName().substring(0, file.getName().lastIndexOf('.'))
                + ".xml";
        // 第六步：创建一个Writer对象，一般创建一FileWriter对象，指定生成的文件名。
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(xmlName), "UTF-8"));
        try {
            // 第七步：调用模板对象的process方法输出文件。
            template.process(dataModel, out);

        } finally {
            // 第八步：关闭流。
            out.close();
        }

    }

    private static String getxmlDirPath(File file) {
        File xmlDir = new File(file.getParent() + "/" + "xml");
        if (!xmlDir.exists() || !xmlDir.isDirectory()) {
            xmlDir.mkdirs();
        }
        return xmlDir.getAbsolutePath();
    }
}
