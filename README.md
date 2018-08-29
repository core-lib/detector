# detector
Project resource recursive search for Java! supports jar library

# How to use ?
# Maven
```
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://www.jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.core-lib</groupId>
    <artifactId>detector</artifactId>
    <version>1.0.1</version>
</dependency>
```

# Gradle
```
allprojects {
	repositories {
		...
		maven { url "https://www.jitpack.io" }
	}
}

dependencies {
        compile 'com.github.core-lib:detector:1.0.1'
}
```

```
ResourceDetector detector = DefaultResourceDetector.Builder.scan("org").build();
Collection<Resource> = detector.detect(ResourceFilter... filters);
```

## The method detect accept variable parameter of ResourceFilter, you can define unlimited resource filtration rule for the detection, But normally, use
```
return chain.doNext(resource);
```
## instead of return true, if the filter accept resource.


#### Find all interface under package named "org" by using a resource filter, normally, almost all resources in the project is class file, so Resource has some convenient methods to get it's class type
```
	@Test
	public void testFindAllInterfaces() throws Exception {
		ResourceDetector detector = DefaultResourceDetector.Builder.scan("org").build();
		Collection<Resource> resources = detector.detect(new ResourceFilter() {

			public boolean accept(Resource resource, ResourceFilterChain chain) {
				try {
					// call isClass() before toClass()!!! in case of ClassNotFoundException thrown
					return resource.isClass() && resource.toClass().isInterface() ? chain.doNext(resource) : false;
				} catch (ClassNotFoundException e) {
					throw new IllegalStateException(e);
				}
			}

		});

		for (Resource resource : resources) {
			System.out.println(resource.toClass());
		}
		System.err.println(resources.size() + " interfaces had been detected");
	}
```

#### By defaults, detection is recursively and jar library included, so every thing under package named "org" in the project including jar library would be detect out
```
	@Test
	public void testJarIncluded() throws Exception {
		ResourceDetector detector = DefaultResourceDetector.Builder.scan("org").build();
		Collection<Resource> resources = detector.detect();
		for (Resource resource : resources) {
			System.out.println(resource);
		}
		System.err.println(resources.size() + " resources had been detected");
	}
```


#### Scan from package named "org" but excluding jar library
```
	@Test
	public void testJarExcluded() throws Exception {
		ResourceDetector detector = DefaultResourceDetector.Builder.scan("org").excludeJar().build();
		Collection<Resource> resources = detector.detect();
		for (Resource resource : resources) {
			System.out.println(resource);
		}
		System.err.println(resources.size() + " resources had been detected");
	}
```

#### By defaults, detection is recursively, but I specify recursive explicitly, the result is all of java class in my  project
```
	@Test
	public void testRecursively() throws Exception {
		ResourceDetector detector = DefaultResourceDetector.Builder.scan("org.qfox").recursively().build();
		Collection<Resource> resources = detector.detect();
		for (Resource resource : resources) {
			System.out.println(resource);
		}
		System.err.println(resources.size() + " resources had been detected");
	}
```

#### There is nothing in package "org.qfox" so it detect out an empty collection by not recursive mode
```
	@Test
	public void testUnrecursive() throws Exception {
		ResourceDetector detector = DefaultResourceDetector.Builder.scan("org.qfox").unrecursive().build();
		Collection<Resource> resources = detector.detect();
		for (Resource resource : resources) {
			System.out.println(resource);
		}
		System.err.println(resources.size() + " resources had been detected");
	}
```

#### Use resource filter to exclude the test-classes, use an anonymous resource filter class or just use MavenTestClassesExcludeFilter
```
	@Test
	public void testFilter() throws Exception {
		ResourceDetector detector = DefaultResourceDetector.Builder.scan("org.qfox").build();
		Collection<Resource> resources = detector.detect(new ResourceFilter() {
			private final String prefix = "file:" + System.getProperty("user.dir") + "/target/test-classes";

			
			 if accepted, do next filter, just not return true!!!
			 
			public boolean accept(Resource resource, ResourceFilterChain chain) {
				return resource.getUrl().toString().startsWith(prefix) ? false : chain.doNext(resource);
			}

		});

		// Equivalent to ...
		// Collection<Resource> resources = detector.detect(new MavenTestClassesExcludeFilter());

		for (Resource resource : resources) {
			System.out.println(resource);
		}
		System.err.println(resources.size() + " resources had been detected");
	}
```
