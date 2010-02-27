package com.izforge.izpack.compiler.helper;

import com.izforge.izpack.api.adaptator.IXMLElement;
import com.izforge.izpack.api.exception.CompilerException;
import com.izforge.izpack.compiler.data.CompilerData;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author Anthonin Bonnefoy
 */
public class AssertionHelper
{
    private String installFile;

    public AssertionHelper(CompilerData compilerData)
    {
        this.installFile = compilerData.getInstallFile();
    }

    /**
     * Create parse error with consistent messages. Includes file name. For use When parent is
     * unknown.
     *
     * @param message Brief message explaining error
     */
    public void parseError(String message) throws CompilerException
    {
        throw new CompilerException(this.installFile + ":" + message);
    }

    /**
     * Create parse error with consistent messages. Includes file name and line # of parent. It is
     * an error for 'parent' to be null.
     *
     * @param parent  The element in which the error occured
     * @param message Brief message explaining error
     */
    public void parseError(IXMLElement parent, String message) throws CompilerException
    {
        throw new CompilerException(this.installFile + ":" + parent.getLineNr() + ": " + message);
    }

    /**
     * Create a chained parse error with consistent messages. Includes file name and line # of
     * parent. It is an error for 'parent' to be null.
     *
     * @param parent  The element in which the error occured
     * @param message Brief message explaining error
     */
    public void parseError(IXMLElement parent, String message, Throwable cause)
            throws CompilerException
    {
        throw new CompilerException(this.installFile + ":" + parent.getLineNr() + ": " + message, cause);
    }

    /**
     * Create a parse warning with consistent messages. Includes file name and line # of parent. It
     * is an error for 'parent' to be null.
     *
     * @param parent  The element in which the warning occured
     * @param message Warning message
     */
    public void parseWarn(IXMLElement parent, String message)
    {
        System.out.println("Warning: " + this.installFile + ":" + parent.getLineNr() + ": " + message);
    }

    /**
     * Checks whether a File instance is a regular file, exists and is readable. Throws appropriate
     * CompilerException to report violations of these conditions.
     *
     * @throws com.izforge.izpack.api.exception.CompilerException
     *          if the file is either not existing, not a regular file or not
     *          readable.
     */
    public void assertIsNormalReadableFile(File fileToCheck, String fileDescription)
            throws CompilerException
    {
        if (fileToCheck != null)
        {
            if (!fileToCheck.exists())
            {
                throw new CompilerException(fileDescription
                        + " does not exist: " + fileToCheck);
            }
            if (!fileToCheck.isFile())
            {
                throw new CompilerException(fileDescription
                        + " is not a regular file: " + fileToCheck);
            }
            if (!fileToCheck.canRead())
            {
                throw new CompilerException(fileDescription
                        + " is not readable by application: " + fileToCheck);
            }
        }
    }
}
