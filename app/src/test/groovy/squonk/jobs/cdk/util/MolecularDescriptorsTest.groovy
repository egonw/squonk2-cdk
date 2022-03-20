/*
 * This Spock specification was generated by the Gradle 'init' task.
 */
package squonk.jobs.cdk.util

import org.openscience.cdk.interfaces.IAtomContainer
import org.openscience.cdk.silent.SilentChemObjectBuilder
import org.openscience.cdk.smiles.SmilesParser
import spock.lang.Specification

import java.util.stream.Collectors

class MolecularDescriptorsTest extends Specification {

    static SmilesParser parser = new SmilesParser(SilentChemObjectBuilder.getInstance());

    def readSdf(file) {
        def reader = MoleculeUtils.createSDFReader(file)
        List mols = []
        while (reader.hasNext()) {
            IAtomContainer molecule = (IAtomContainer)reader.next();
            mols << new MoleculeObject(molecule)
        }
        return mols
    }

    def "test xlogp"() {

        setup:
        def d = MolecularDescriptors.Descriptor.XLogP.create()
        def mol = parser.parseSmiles("C1C=CC=CC1C");
        def mo = new MoleculeObject(mol)

        when:
        d.calculate(mo)
        def result = mo.getProperty('XLogP_CDK')

        then:
        result != null
        d.executionStats.size() == 1
        d.executionStats['CDK.XLogP'] == 1
    }

    def "test alogp"() {

        setup:
        def d = MolecularDescriptors.Descriptor.ALogP.create()
        def mol = parser.parseSmiles("C1C=CC=CC1C");
        def mo = new MoleculeObject(mol)

        when:
        d.calculate(mo)
        def result1 = mo.getProperty('ALogP_CDK')
        def result2 = mo.getProperty('AMR_CDK')

        then:
        result1 != null
        result2 != null
        d.executionStats.size() == 1
        d.executionStats['CDK.ALogP'] == 1
    }

    def "test hba"() {

        setup:
        def d = MolecularDescriptors.Descriptor.HBondAcceptorCount.create()
        def mol = parser.parseSmiles("C1C=CC=CC1C");
        def mo = new MoleculeObject(mol)

        when:
        d.calculate(mo)
        def result = mo.getProperty('HBA_CDK')

        then:
        result != null
        d.executionStats.size() == 1
        d.executionStats['CDK.HBondAcceptorCount'] == 1
    }

    def "test hbd"() {

        setup:
        def d = MolecularDescriptors.Descriptor.HBondDonorCount.create()
        def mol = parser.parseSmiles("C1C=CC=CC1C");
        def mo = new MoleculeObject(mol)

        when:
        d.calculate(mo)
        def result = mo.getProperty('HBD_CDK')

        then:
        result != null
        d.executionStats.size() == 1
        d.executionStats['CDK.HBondDonorCount'] == 1
    }

    def "test weiner"() {

        setup:
        def d = MolecularDescriptors.Descriptor.WienerNumbers.create()
        def mol = parser.parseSmiles("C1C=CC=CC1C");
        def mo = new MoleculeObject(mol)

        when:
        d.calculate(mo)
        def result1 = mo.getProperty('WienerPath_CDK')
        def result2 = mo.getProperty('WienerPolarity_CDK')

        then:
        result1 != null
        result2 != null
        d.executionStats.size() == 1
        d.executionStats['CDK.WienerNumbers'] == 1
    }

    def "test stream"() {

        setup:
        def d = MolecularDescriptors.Descriptor.XLogP.create()
        List mols = readSdf('../data/dhfr_3d.sdf')

        when:
        def stream = d.calculate(mols.stream())
        def results = stream.collect(Collectors.toList())
        def num = results.size()
        def mol0 = results[0]
        println(mol0.getProperties())
        def result0 = mol0.getProperty('XLogP_CDK')

        then:
        num == 756
        Math.abs(result0 - 0.015) < 0.01
        d.executionStats.size() == 1
        d.executionStats['CDK.XLogP'] == 756
    }
}
