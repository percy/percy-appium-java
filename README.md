# percy-appium-app

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.percy/percy-appium-app/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.percy/percy-appium-app)
![Test](https://github.com/percy/percy-appium-java/workflows/Test/badge.svg)

[Percy](https://percy.io) visual testing for Java Appium.

## Installation

npm install `@percy/cli`:

```sh-session
$ npm install --save-dev @percy/cli
```
Note: Minimum required version for @percy/cli is 1.15.0 for this package to work correctly.

Add percy-appium-app to your project dependencies. If you're using Maven:

``` xml
<dependency>
  <groupId>io.percy</groupId>
  <artifactId>percy-appium-app</artifactId>
  <version>1.0.1</version>
</dependency>
```

If you're using a different build system, see https://search.maven.org/artifact/io.percy/percy-appium-app for details for your specific system.

> Note: This package is tested on Java versions 8, 11, 12, 13, 17 as part of unit tests. It should ideally work on all Java 8+ versions.


## Usage

This is an example test using the `percy.screenshot` function.

``` java
// import ...
import io.percy.appium.AppPercy;

public class Example {

  public static void main(String[] args) throws MalformedURLException, InterruptedException {
    DesiredCapabilities caps = new DesiredCapabilities();
    // Add caps here

    AndroidDriver<AndroidElement> driver = new AndroidDriver<AndroidElement>(
      new URL("http://hub.browserstack.com/wd/hub"), caps);

    AppPercy percy = new AppPercy(driver);
    percy.screenshot("First Screenshot");
    driver.quit();
  }
}
```

Running the test above normally will result in the following log:

```sh-session
[percy] Percy is not running, disabling screenshots
```

When running with [`percy
app:exec`](https://github.com/percy/cli/tree/master/packages/cli-app#percy-appexec), and your project's
`PERCY_TOKEN`, a new Percy build will be created and screenshots will be uploaded to your project.

```sh-session
$ export PERCY_TOKEN=[your-project-token]
$ percy exec -- [java test command]
[percy] Percy has started!
[percy] Created build #1: https://percy.io/[your-project]
[percy] Screenshot taken "Java example"
[percy] Stopping percy...
[percy] Finalized build #1: https://percy.io/[your-project]
[percy] Done!
```

## Configuration

The screenshot method arguments:

``` java
  ScreenshotOptions options = new ScreenshotOptions();
  // Set options here
  percy.screenshot(name, fullScreen, options)
```

- `name` (**required**) - The screenshot name; must be unique to each screenshot
- Additional screenshot options (overrides any project options):
  - `fullScreen ` - (**optional**) It indicates if the app is a full screen
  - `options` - (**optional**) configure screenshot using below setter:

| Setter Method  | Description |
| ------------- | ------------- |
| setDeviceName(String deviceNameParam)  | Device name on which screenshot is taken  |
| setStatusBarHeight(Integer statusBarHeightParam)  | Height of status bar for the device  |
| setNavBarHeight(Integer navBarHeightParam)  | Height of navigation bar for the device  |
| setOrientation(String orientationParam)  | Orientation of the application  |
| setFullPage(Boolean fullPageParam)  | Only supported on App Automate driver sessions [ needs @percy/cli 1.20.2+ ]  |
| setScreenLengths(Integer screenLengthsParam)  | Max screen lengths for fullPage [ needs @percy/cli 1.20.2+ ]  |
| setTopScrollviewOffset(Integer topScrollviewOffsetParam)  | Offset from top of scrollview [ needs @percy/cli 1.20.2+ ]  |
| setBottomScrollviewOffset(Integer bottomScrollviewOffsetParam)  | Offset from bottom of scrollview [ needs @percy/cli 1.20.2+ ]  |
| setFullScreen(Boolean fullScreenParam)  | Indicate whether app is full screen; boolean  |
| setScrollableXpath(String scrollableXpath)  | Scrollable element xpath for fullpage [ needs @percy/cli 1.20.2+ ]  |
| setScrollableId(String scrollableId)  | Scrollable element accessibility id for fullpage [ needs @percy/cli 1.20.2+ ]  |
| setIgnoreRegionXpaths(List<String> ignoreRegionXpaths)  | Elements xpaths that user want to ignore in visual diff [ needs @percy/cli 1.23.0+ ]  |
| setIgnoreRegionAccessibilityIds(List<String> ignoreRegionAccessibilityIds)  | Elements accessibility_ids that user want to ignore in visual diff [ needs @percy/cli 1.23.0+ ]  |
| setIgnoreRegionAppiumElements(List<MobileElement> ignoreRegionAppiumElements)  | Appium elements that user want to ignore in visual diff [ needs @percy/cli 1.23.0+ ]  |
| setCustomIgnoreRegions(List<Region> customIgnoreRegions)  | Custom locations that user want to ignore in visual diff [ needs @percy/cli 1.23.0+ ]
      - IgnoreRegion:-
        - Description: This class represents a rectangular area on a screen that needs to be ignored for visual diff.
        - constructor:-
          ```
          var ignoreRegion = new IgnoreRegion();
          ignoreRegion.setTop() = top;
          ignoreRegion.setBottom = bottom;
          ignoreRegion.setLeft = left;
          ignoreRegion.setRight = right;
          ```  |
| setConsiderRegionXpaths(List<String> considerRegionXpaths)  | Elements xpaths that user want to consider in visual diff [ needs @percy/cli 1.23.0+ ]  |
| setConsiderRegionAccessibilityIds(List<String> considerRegionAccessibilityIds)  | Elements accessibility_ids that user want to consider in visual diff [ needs @percy/cli 1.23.0+ ]  |
| setConsiderRegionAppiumElements(List<MobileElement> considerRegionAppiumElements)  | Appium elements that user want to consider in visual diff [ needs @percy/cli 1.23.0+ ]  |
| setCustomConsiderRegions(List<Region> customConsiderRegions)  | Custom locations that user want to consider in visual diff [ needs @percy/cli 1.23.0+ ]
      - Region:-
        - Description: This class represents a rectangular area on a screen that needs to be considered for visual diff.
        - constructor:-
          ```
          var region = new Region();
          region.setTop() = top;
          region.setBottom = bottom;
          region.setLeft = left;
          region.setRight = right;
          ```  |

## Running with Hybrid Apps

For a hybrid app, we need to switch to native context before taking screenshot.

- Add a helper method similar to following for say flutter based hybrid app:
```java
public void percyScreenshotFlutter(AppPercy appPercy, AppiumDriver driver, String name, ScreenshotOptions options) {
    // switch to native context
    driver.context("NATIVE_APP");
    appPercy.screenshot(name, options);
    // switch back to flutter context
    driver.context("FLUTTER");
}
```

- Call percyScreenshotFlutter helper function when you want to take screenshot.
```java
percyScreenshotFlutter(appPercy, driver, name, options);
```

> Note: 
>
> For other hybrid apps the `driver.context("FLUTTER");` would change to context that it uses like say WEBVIEW etc.
>
### Migrating Config

If you have a previous Percy configuration file, migrate it to the newest version with the
[`config:migrate`](https://github.com/percy/cli/tree/master/packages/cli-config#percy-configmigrate-filepath-output) command:

```sh-session
$ percy config:migrate
```
## Running Percy on Automate
`percyScreenshot(driver, name, options)` [ needs @percy/cli 1.27.0-beta.0+ ];

This is an example test using the `percy.Screenshot` method.

``` java
// import ...
import io.percy.appium.PercyOnAutomate;

public class Example {

  public static void main(String[] args) throws MalformedURLException, InterruptedException {
    DesiredCapabilities caps = new DesiredCapabilities();
    // Add caps here

    WebDriver driver = new RemoteWebDriver(new URL(URL), caps);

    PercyOnAutomate percy = new PercyOnAutomate(driver);
    percy.screenshot("First Screenshot");
    driver.quit();
  }
}
```

- `driver` (**required**) - A appium driver instance
- `name` (**required**) - The screenshot name; must be unique to each screenshot
- `options` (**optional**) - There are various options supported by percy_screenshot to server further functionality.
  - `freezeAnimation` - Boolean value by default it falls back to `false`, you can pass `true` and percy will freeze image based animations.
  - `percyCSS` - Custom CSS to be added to DOM before the screenshot being taken. Note: This gets removed once the screenshot is taken.
  - `ignoreRegionXpaths` - elements in the DOM can be ignored using xpath
  - `ignoreRegionSelectors` - elements in the DOM can be ignored using selectors.
  - `ignoreRegionAppiumElements` - elements can be ignored using appium_elements.
  - `customIgnoreRegions` - elements can be ignored using custom boundaries
    - IgnoreRegion:-
      - Description: This class represents a rectangular area on a screen that needs to be ignored for visual diff.

      - Constructor:
        ```
        init(self, top, bottom, left, right)
        ```
      - Parameters:
        `top` (int): Top coordinate of the ignore region.
        `bottom` (int): Bottom coordinate of the ignore region.
        `left` (int): Left coordinate of the ignore region.
        `right` (int): Right coordinate of the ignore region.
      - Raises:ValueError: If top, bottom, left, or right is less than 0 or top is greater than or equal to bottom or left is greater than or equal to right.
      - valid: Ignore region should be within the boundaries of the screen.

### Creating Percy on automate build
Note: Automate Percy Token starts with `auto` keyword. The command can be triggered using `exec` keyword.
```sh-session
$ export PERCY_TOKEN=[your-project-token]
$ percy exec -- [python test command]
[percy] Percy has started!
[percy] [Python example] : Starting automate screenshot ...
[percy] Screenshot taken "Python example"
[percy] Stopping percy...
[percy] Finalized build #1: https://percy.io/[your-project]
[percy] Done!
```

Refer to docs here: [Percy on Automate](https://docs.percy.io/docs/integrate-functional-testing-with-visual-testing)
