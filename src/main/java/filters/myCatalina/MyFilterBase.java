package filters.myCatalina;


import javax.servlet.Filter;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import java.util.Enumeration;

/**
 * Base class for filters that provides generic initialisation and a simple
 * no-op destruction.
 */
public abstract class MyFilterBase implements Filter {

//    protected static final StringManager sm = StringManager.getManager(MyFilterBase.class);

//    protected abstract Log getLogger();


    /**
     * Iterates over the configuration parameters and either logs a warning,
     * or throws an exception for any parameter that does not have a matching
     * setter in this filter.
     *
     * @param filterConfig The configuration information associated with the
     *                     filter instance being initialised
     * @throws ServletException if {@link #isConfigProblemFatal()} returns
     *                          {@code true} and a configured parameter does not
     *                          have a matching setter
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> paramNames = filterConfig.getInitParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            if (!MyIntrospectionUtils.setProperty(this, paramName,
                    filterConfig.getInitParameter(paramName))) {
                String msg = "filterbase.noSuchProperty";
                if (isConfigProblemFatal()) {
                    throw new ServletException(msg);
                } else {
//                    getLogger().warn(msg);
                }
            }
        }
    }

    /**
     * Determines if an exception when calling a setter or an unknown
     * configuration attribute triggers the failure of the this filter which in
     * turn will prevent the web application from starting.
     *
     * @return <code>true</code> if a problem should trigger the failure of this
     * filter, else <code>false</code>
     */
    protected boolean isConfigProblemFatal() {
        return false;
    }
}
