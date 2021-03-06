package com.izforge.izpack.merge.resolve;

import com.izforge.izpack.api.merge.Mergeable;
import com.izforge.izpack.core.container.TestMergeContainer;
import com.izforge.izpack.matcher.MergeMatcher;
import com.izforge.izpack.merge.jar.JarMerge;
import com.izforge.izpack.test.Container;
import com.izforge.izpack.test.junit.PicoRunner;
import com.izforge.izpack.util.FileUtil;
import org.hamcrest.collection.IsCollectionContaining;
import org.hamcrest.core.Is;
import org.hamcrest.core.IsNot;
import org.hamcrest.number.IsGreaterThan;
import org.hamcrest.text.StringContains;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;

/**
 * Test of path resolver
 *
 * @author Anthonin Bonnefoy
 */
@RunWith(PicoRunner.class)
@Container(TestMergeContainer.class)
public class PathResolverTest
{
    private PathResolver pathResolver;
    private MergeableResolver mergeableResolver;

    public PathResolverTest(PathResolver pathResolver, MergeableResolver mergeableResolver)
    {
        this.pathResolver = pathResolver;
        this.mergeableResolver = mergeableResolver;
    }

    @Test
    public void testGetMergeableFromJar() throws Exception
    {
        List<Mergeable> jarMergeList = pathResolver.getMergeableFromPath("junit/framework");
        assertThat(jarMergeList.size(), Is.is(1));
        Mergeable jarMerge = jarMergeList.get(0);
        assertThat(jarMerge, Is.is(JarMerge.class));
        assertThat(jarMerge, MergeMatcher.isMergeableContainingFiles("junit/framework/Assert.class",
                "junit/framework/AssertionFailedError.class"
        ));
    }

    @Test
    public void testResolvePathOfJar()
    {
        Set<URL> urlList = pathResolver.resolvePath("com/izforge");
        assertThat(urlList.size(), new IsGreaterThan<Integer>(1));
    }

    @Test
    public void testResolvePathOfFileAndJar() throws Exception
    {
        Set<URL> urlList = pathResolver.resolvePath("META-INF/MANIFEST.MF");
        assertThat(getListPathFromListURL(urlList), IsCollectionContaining.hasItems(
                StringContains.containsString("jar!"),
                IsNot.not(StringContains.containsString("jar!"))
        ));
    }

    @Test
    public void testResolvePathOfDirectory() throws Exception
    {
        Collection<URL> urlList = pathResolver.resolvePath("com/izforge/izpack/merge/");
        assertThat(getListPathFromListURL(urlList), IsCollectionContaining.hasItems(
                IsNot.not(StringContains.containsString("jar!"))
        ));
    }

    @Test
    public void ftestGetMergeableFromFile() throws Exception
    {
        List<Mergeable> mergeables = pathResolver.getMergeableFromPath("com/izforge/izpack/merge/file/FileMerge.class");
        Mergeable mergeable = mergeables.get(0);
        assertThat(mergeable, MergeMatcher.isMergeableContainingFiles("com/izforge/izpack/merge/file/FileMerge.class")
        );
    }

    @Test
    public void testGetMergeableFromFileWithDestination() throws Exception
    {
        List<Mergeable> mergeables = pathResolver.getMergeableFromPath("com/izforge/izpack/merge/file/FileMerge.class", "a/dest/FileMerge.class");
        Mergeable mergeable = mergeables.get(0);
        assertThat(mergeable, MergeMatcher.isMergeableContainingFiles("a/dest/FileMerge.class")
        );
    }

    @Test
    public void testGetMergeableFromDirectory() throws Exception
    {
        List<Mergeable> mergeables = pathResolver.getMergeableFromPath("com/izforge/izpack/merge/");
        assertThat(mergeables, IsCollectionContaining.hasItem(
                MergeMatcher.isMergeableContainingFiles("com/izforge/izpack/merge/resolve/PathResolver.class")));
    }

    @Test
    public void testGetMergeableFromDirectoryWithDestination() throws Exception
    {
        List<Mergeable> mergeables = pathResolver.getMergeableFromPath("com/izforge/izpack/merge/", "a/dest/");
        assertThat(mergeables,
                IsCollectionContaining.hasItem(
                        MergeMatcher.isMergeableContainingFiles("a/dest/resolve/PathResolver.class")));
    }

    @Test
    public void testGetMergeableFromPackage() throws Exception
    {
        List<Mergeable> mergeables = pathResolver.getMergeableFromPackageName("com.izforge.izpack.merge");
        assertThat(mergeables, IsCollectionContaining.hasItem(
                MergeMatcher.isMergeableContainingFiles("com/izforge/izpack/merge/resolve/PathResolver.class")));
    }

    private Collection<String> getListPathFromListURL(Collection<URL> urlList)
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        for (URL url : urlList)
        {
            arrayList.add(url.getPath());
        }
        return arrayList;
    }

    @Test
    public void testIsJarWithURL() throws Exception
    {
        URL fileResource = ClassLoader.getSystemResource("com/izforge/izpack/merge/AbstractMerge.class");
        URL jarResource = ClassLoader.getSystemResource("com/izforge/izpack/merge/test/jar-hellopanel-1.0-SNAPSHOT.jar");
        assertThat(ResolveUtils.isJar(
                fileResource),
                Is.is(false));
        assertThat(ResolveUtils.isJar(
                jarResource),
                Is.is(true));
    }

    @Test
    public void testIsJarWithFile() throws Exception
    {
        File fileResource = FileUtil.convertUrlToFile(ClassLoader.getSystemResource("com/izforge/izpack/merge/AbstractMerge.class"));
        File jarResource = FileUtil.convertUrlToFile(ClassLoader.getSystemResource("com/izforge/izpack/merge/test/jar-hellopanel-1.0-SNAPSHOT.jar"));
        assertThat(ResolveUtils.isJar(
                fileResource),
                Is.is(false));
        assertThat(ResolveUtils.isJar(
                jarResource),
                Is.is(true));
    }


    @Test
    public void pathResolverShouldTransformClassNameToPackagePath() throws Exception
    {
        String pathFromClassName = ResolveUtils.getPanelsPackagePathFromClassName("com.test.sora.UneClasse");
        assertThat(pathFromClassName, Is.is("com/test/sora/"));
    }

    @Test
    public void pathResolverShouldReturnDefaultPackagePath() throws Exception
    {
        String pathFromClassName = ResolveUtils.getPanelsPackagePathFromClassName("UneClasse");
        assertThat(pathFromClassName, Is.is("com/izforge/izpack/panels/"));
    }

}
