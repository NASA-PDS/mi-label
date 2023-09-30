// Copyright 2020, California Institute of Technology ("Caltech").
// U.S. Government sponsorship acknowledged.
//
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
//
// * Redistributions of source code must retain the above copyright notice,
// this list of conditions and the following disclaimer.
// * Redistributions must reproduce the above copyright notice, this list of
// conditions and the following disclaimer in the documentation and/or other
// materials provided with the distribution.
// * Neither the name of Caltech nor its operating division, the Jet Propulsion
// Laboratory, nor the names of its contributors may be used to endorse or
// promote products derived from this software without specific prior written
// permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
// ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
// LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
// CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
// SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
// INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
// CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
// ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
// POSSIBILITY OF SUCH DAMAGE.

package gov.nasa.pds.imaging.generate;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import gov.nasa.pds.imaging.generate.cli.options.Flag;
import gov.nasa.pds.imaging.generate.cli.options.InvalidOptionException;
import gov.nasa.pds.imaging.generate.label.PDS3Label;
import gov.nasa.pds.imaging.generate.label.PDSObject;
import gov.nasa.pds.imaging.generate.readers.ParserType;
import gov.nasa.pds.imaging.generate.util.Debugger;
import gov.nasa.pds.imaging.generate.util.ToolInfo;
import gov.nasa.pds.imaging.generate.util.Utility;

/**
 * Class used as Command-line interface endpoint. Parses command-line arguments and sends workflow
 * onto the Generator object.
 *
 * @author jpadams
 *
 */
public class GenerateLauncher {

  /** Logger. **/
  private static Logger log = Logger.getLogger(GenerateLauncher.class.getName());

  private String basePath;
  private List<String> lblList;
  private File templateFile;
  private File outputPath;

  // By default, launcher will assume you want to output an XML document
  private boolean isXML;

  private List<Generator> generatorList;

  private List<String> includePaths;

  public GenerateLauncher() {
    this.basePath = null;
    this.generatorList = new ArrayList<>();
    this.lblList = new ArrayList<>();
    this.outputPath = null;
    this.templateFile = null;
    this.isXML = true;
    this.includePaths = new ArrayList<>();
  }

  /**
   * Displays tool usage.
   *
   */
  public final void displayHelp() {
    final int maxWidth = 80;
    final HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp(maxWidth, "generate <options>", null, Flag.getOptions(), null);
  }

  /**
   * Displays the current version and disclaimer notice.
   *
   */
  public final void displayVersion() {
    System.err.println("\n" + ToolInfo.getName());
    System.err.println(ToolInfo.getVersion());
    System.err.println("Release Date: " + ToolInfo.getReleaseDate());
    System.err.println(ToolInfo.getCopyright() + "\n");
  }

  public final void generate() {
    try {
      for (Generator generator : this.generatorList) {
        // By using Launcher, output will go to a file, so argument = false
        generator.generate(false);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private String getConfigPath() {
    return (new File(System.getProperty("java.class.path"))).getParentFile().getParent() + "/conf";
  }

  /**
   * A method to parse the command-line arguments.
   *
   * @param args The command-line arguments
   * @return A class representation of the command-line arguments
   *
   * @throws ParseException If there was an error during parsing.
   */
  public final CommandLine parse(final String[] args) throws ParseException {
    final CommandLineParser parser = new GnuParser();
    return parser.parse(Flag.getOptions(), args);
  }

  /**
   * Examines the command-line arguments passed into the Harvest Tool and takes the appropriate
   * action based on what flags were set.
   *
   * @param line A class representation of the command-line arguments.
   *
   * @throws Exception If there was an error while querying the options that were set on the
   *         command-line.
   */
  public final void query(final CommandLine line) throws Exception {
    final List<Option> processedOptions = Arrays.asList(line.getOptions());

    for (final Option o : processedOptions) {
      if (o.getOpt().equals(Flag.HELP.getShortName())) {
        displayHelp();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.VERSION.getShortName())) {
        displayVersion();
        System.exit(0);
      } else if (o.getOpt().equals(Flag.PDS3.getShortName())) {
        this.lblList = new ArrayList<>();
        for (String path : o.getValuesList()) {
          Debugger.debug(Utility.getAbsolutePath(path));
          this.lblList.add(Utility.getAbsolutePath(path));
        }
      } else if (o.getOpt().equals(Flag.TEMPLATE.getShortName())) {
        this.templateFile = new File(Utility.getAbsolutePath(o.getValue().trim()));
      } else if (o.getOpt().equals(Flag.OUTPUT.getShortName())) {
        this.outputPath = new File(Utility.getAbsolutePath(o.getValue().trim()));
      } else if (o.getOpt().equals(Flag.BASEPATH.getShortName())) {
        this.basePath = o.getValue().trim();
      } else if (o.getOpt().equals(Flag.TEXTOUT.getShortName())) {
        this.isXML = false;
      } else if (o.getOpt().equals(Flag.INCLUDES.getShortName())) {
        setIncludePaths(o.getValuesList());
      } else if (o.getOpt().equals(Flag.DEBUG.getShortName())) {
        Debugger.debugFlag = true;
      }

    }

    // First check we have a Template File
    if (this.templateFile == null) { // Throw error if no template file
                                     // specified
      throw new InvalidOptionException("Missing -t flag.  Template file must be specified.");
    }

    // Now let's check where the output is going
    if (this.outputPath != null && !this.outputPath.isDirectory()) {
      if (this.outputPath.isFile()) {
        throw new InvalidOptionException(
            "Output path is invalid. " + "Must be existing directory or new path.");
      } else {
        this.outputPath.mkdirs();
      }
    }

    // Let's default to the one label if -p flag was specified,
    // otherwise loop through the lbl list
    if (this.lblList == null) {
      throw new InvalidOptionException(
          "Missing -p or -l flags.  " + "One or many PDS3 label must be specified.");
    } else {
      String filepath;
      PDS3Label pdsLabel;
      PDSObject pdsObj;
      File outputFile;
      String parserType = System.getProperty("pds.generate.parser.type");
      for (String lbl : this.lblList) {
        // Make sure the lbl exists
        if ((new File(lbl)).isFile()) {
          // Set the pds3 lable object
          pdsLabel = new PDS3Label(lbl);
          if (parserType != null) {
            if ("vicar".equalsIgnoreCase(parserType)) {
              pdsLabel.setParserType(ParserType.VICAR);
            } else if ("product-tools".equalsIgnoreCase(parserType)) {
              pdsLabel.setParserType(ParserType.PRODUCT_TOOLS);
              pdsLabel.setIncludePaths(includePaths);
            }
          }
          pdsObj = pdsLabel;
          pdsObj.setMappings();

          // Build up the output filepath
          outputFile = new File(lbl);

          // Find out the file suffix
          String suffix = "";
          if (this.isXML)
            suffix = ".xml";
          else
            suffix = ".txt";

          // Let's get the output file ready
          filepath = outputFile.getParent();

          if (this.outputPath != null) {
            if (this.basePath != null) {
              filepath =
                  this.outputPath.getAbsolutePath() + "/" + filepath.replace(this.basePath, "");
            } else {
              filepath = this.outputPath.getAbsolutePath();
            }

          }

          filepath = filepath + "/" + outputFile.getName().split("\\.")[0] + suffix;

          // If the output path is not an XML file, set boolean
          if (!filepath.endsWith("xml"))
            this.isXML = false;

          outputFile = new File(filepath);

          Debugger.debug(outputFile.getAbsolutePath());
          this.generatorList.add(new Generator(pdsObj, this.templateFile, outputFile, this.isXML));
        } else {
          log.warning(lbl + " does not exist.");
        }
      }
    }

  }

  /**
   * Set the paths to search for files referenced by pointers.
   * <p>
   * Default is to always look first in the same directory as the label, then search specified
   * directories.
   *
   * @param i List of paths
   */
  public void setIncludePaths(List<String> i) {
    this.includePaths = new ArrayList<>(i);
    while (this.includePaths.remove(""));
  }

  /**
   * @param args
   */
  public static void main(final String[] args) {
    if (args.length == 0) {
      System.out.println("\nType 'generate -h' for usage");
      System.exit(0);
    }
    try {
      final GenerateLauncher launcher = new GenerateLauncher();
      final CommandLine commandline = launcher.parse(args);
      launcher.query(commandline);
      launcher.generate();
      // launcher.closeHandlers();
    } catch (final ParseException pEx) {
      System.err.println("Command-line parse failure: " + pEx.getMessage());
      System.exit(1);
    } catch (final Exception e) {
      e.printStackTrace();
      System.exit(1);
    }
  }

}
