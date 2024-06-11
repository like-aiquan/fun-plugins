package likeai.fun.plugins;

import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.scripting.xmltags.XMLLanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.w3c.dom.Text;

/**
 * @author likeai
 */
public class XmlParseHintSqlDriver extends XMLLanguageDriver implements LanguageDriver {
    /**
     * User define sql comment
     * Ognl support: tid:${@org.slf4j.MDC@get('tid')}
     * Of course, you can also directly pass the required tid as a parameter.
     */
    @Override
    public SqlSource createSqlSource(Configuration configuration, XNode script, Class<?> parameterType) {
        // I have not specifically analyzed the pros and cons of several methods,
        // or even how much impact they have on system performance.
        // Use with caution!
        Text comment = script.getNode().getOwnerDocument().createTextNode(String.format("/* %s */", "XmlParseHintSqlDriver"));
        script.getNode().appendChild(comment);
        return super.createSqlSource(configuration, script, parameterType);
    }

    @Override
    public SqlSource createSqlSource(Configuration configuration, String script, Class<?> parameterType) {
        return super.createSqlSource(configuration, modifyScript(script), parameterType);
    }

    private String modifyScript(String script) {
        // 在这里根据需求修改SQL语句
        return script + " /* XmlParseHintSqlDriver */";
    }
}