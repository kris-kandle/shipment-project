package dk.dtu.gbar.gitlab.shipment.GUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dk.dtu.gbar.gitlab.shipment.LogIn;
import dk.dtu.gbar.gitlab.shipment.LogisticsCompany;
import dk.dtu.gbar.gitlab.shipment.Searcher;
import dk.dtu.gbar.gitlab.shipment.persistence.models.Journey;

import javax.swing.JTable;

public class ClientScreen extends JFrame implements PropertyChangeListener {


    private LoginScreen parentWindow;
    private JourneyRegisterScreen journeyRegisterScreen;
    ExamineScreen examineScreen;
    private LogisticsCompany logisticsCompany;
    private LogIn loggedIn;
    private JPanel panelMainMenuFunctions;
    private JButton btnLogOut;
    private JRadioButton btnShowConcluded;
    private JRadioButton btnShowCurrent;
    private JRadioButton btnShowAll;
    private JButton btnSearch;
    private JButton btnExamine;
    private JButton btnRegisterJourney;
    private JTable tblJourneys;
    private DefaultTableModel clientJourneys;
    private String keyword;
    private Searcher search;
    private List<Journey> journeys;


    ///
    public ClientScreen(LoginScreen parentWindow, LogisticsCompany logisticsCompany, LogIn loggedIn) {
        this.parentWindow = parentWindow;
        this.logisticsCompany = logisticsCompany;
        this.loggedIn = loggedIn;
        this.search = new Searcher(logisticsCompany);
        journeys = (List<Journey>) loggedIn.getLoggedInClient().getClientsJourneys();
        keyword = "";

        logisticsCompany.addObserver(this);

        initialize();
    }

    private void initialize() {
        panelMainMenuFunctions = new JPanel();
        parentWindow.addPanel(panelMainMenuFunctions);
        panelMainMenuFunctions.setLayout(null);
        panelMainMenuFunctions.setBorder(BorderFactory.createTitledBorder("Main Menu"));

        JTextField txtKeywordSearch = new JTextField(30);
        JLabel lblKeywordSearch = new JLabel("Keyword");

        JTextField txtCargoKeywordSearch = new JTextField(30);
        JLabel lblCargoKeywordSearch = new JLabel("Cargo");

        JTextField txtOriginKeywordSearch = new JTextField(30);
        JLabel lblOriginKeywordSearch = new JLabel("Origin");

        JTextField txtDestinationKeywordSearch = new JTextField(30);
        JLabel lblDestinationKeywordSearch = new JLabel("Destination");

        btnSearch = new JButton("Search");
        btnSearch.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                keyword = txtKeywordSearch.getText();
                List searchResults = search.journeySearchByString(journeys, keyword);
                if (txtFieldNotEmpty(txtCargoKeywordSearch)) {
                    searchResults = filterSearchBy(searchResults, search.cargoContains(txtCargoKeywordSearch.getText()));
                }
                if (txtFieldNotEmpty(txtOriginKeywordSearch)) {
                    searchResults = filterSearchBy(searchResults, search.originContains(txtOriginKeywordSearch.getText()));
                }
                if (txtFieldNotEmpty(txtDestinationKeywordSearch)) {
                    searchResults = filterSearchBy(searchResults, search.destinationContains(txtDestinationKeywordSearch.getText()));
                }
                clientJourneys.setRowCount(0);
                display(searchResults);
                //Checks what's in the txtKeywordSearch as well as if showConcluded and showCurrent are enabled
                //pulls up journeys based on keyword and showConcluded and showCurrent
            }

            private List filterSearchBy(List searchResults, Predicate predicate) {
                return search.search(searchResults, predicate);
            }

            private boolean txtFieldNotEmpty(JTextField txtCargoKeywordSearch) {
                return txtCargoKeywordSearch.getText().length() > 0;
            }


        });

        btnLogOut = new JButton("Log Out");
        btnLogOut.setLocation(290, 11);
        btnLogOut.setSize(150, 29);
        btnLogOut.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                parentWindow.setVisible(true);
                loggedIn.logOut();
            }
        });

        btnShowConcluded = new JRadioButton("Show Concluded");
        btnShowConcluded.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnShowCurrent.setSelected(false);
                btnShowAll.setSelected(false);
                clientJourneys.setRowCount(0);
                journeys = search.getConcludedJourneys((List<Journey>) loggedIn.getLoggedInClient().getClientsJourneys());
                List searchResults = search.journeySearchByString(journeys, keyword);
                display(searchResults);
            }
        });

        btnShowCurrent = new JRadioButton("Show Current");
        btnShowCurrent.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnShowConcluded.setSelected(false);
                btnShowAll.setSelected(false);
                clientJourneys.setRowCount(0);
                journeys = search.getCurrentJourneys((List<Journey>) loggedIn.getLoggedInClient().getClientsJourneys());
                List<Journey> searchResults = search.journeySearchByString(journeys, keyword);
                display(searchResults);
            }
        });

        btnShowAll = new JRadioButton("Show All");
        btnShowAll.setLocation(18, 183);
        btnShowAll.setSize(77, 29);
        btnShowAll.setSelected(true);
        btnShowAll.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnShowConcluded.setSelected(false);
                btnShowCurrent.setSelected(false);
                clientJourneys.setRowCount(0);
                journeys = (List<Journey>) loggedIn.getLoggedInClient().getClientsJourneys();
                List searchResults = search.journeySearchByString(journeys, keyword);
                display(searchResults);
            }
        });

        clientJourneys = new DefaultTableModel();
        clientJourneys.addColumn("Origin");
        clientJourneys.addColumn("Destination");
        clientJourneys.addColumn("Cargo");
        clientJourneys.addColumn("Journey ID");
        display((List<Journey>) loggedIn.getLoggedInClient().getClientsJourneys());
        /*for (Journey journey : loggedIn.getLoggedInClient().getClientsJourneys()) {
            clientJourneys.addRow(new Object[]{journey.getJourneyOrigin().getName(), journey.getJourneyDestination().getName(), journey.getContainerContent(), journey.getId()});
        }*/


        JScrollPane scrollJourneys = new JScrollPane();
        scrollJourneys.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        tblJourneys = new JTable(clientJourneys);
        scrollJourneys.setViewportView(tblJourneys);


        btnRegisterJourney = new JButton("Register New Journey");
        btnRegisterJourney.setLocation(21, 35);
        btnRegisterJourney.setSize(173, 29);
        btnRegisterJourney.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
                journeyRegisterScreen.setVisible(true);
                journeyRegisterScreen.setNewOnes(new ArrayList<>());
            }
        });


        btnExamine = new JButton("Examine");
        btnExamine.setBounds(290, 428, 150, 29);
        btnExamine.addActionListener(e -> {
            if (tblJourneys.getSelectedRow() != -1) {
                int journeyID = (int) clientJourneys.getValueAt(tblJourneys.getSelectedRow(), 3);
                int journeyIndex = tblJourneys.getSelectedRow();
                setVisible(false);
                this.examineScreen = new ExamineScreen(parentWindow, this, loggedIn, logisticsCompany, journeyID, journeyIndex);
                examineScreen.setVisible(true);
            }
        });

        btnSearch.setBounds(290, 74, 150, 29);
        txtKeywordSearch.setBounds(102, 75, 130, 26);
        lblKeywordSearch.setBounds(21, 75, 83, 26);

        txtCargoKeywordSearch.setBounds(102, 105, 130, 26);
        lblCargoKeywordSearch.setBounds(21, 105, 83, 26);
        txtOriginKeywordSearch.setBounds(102, 135, 130, 26);
        lblOriginKeywordSearch.setBounds(21, 135, 83, 26);
        txtDestinationKeywordSearch.setBounds(322, 135, 130, 26);
        lblDestinationKeywordSearch.setBounds(241, 135, 83, 26);

        btnShowConcluded.setBounds(18, 158, 129, 29);
        btnShowCurrent.setBounds(149, 158, 105, 29);

        scrollJourneys.setSize(338, 214);
        scrollJourneys.setLocation(102, 203);

        panelMainMenuFunctions.add(lblKeywordSearch);
        panelMainMenuFunctions.add(txtKeywordSearch);
        panelMainMenuFunctions.add(txtDestinationKeywordSearch);
        panelMainMenuFunctions.add(lblDestinationKeywordSearch);
        panelMainMenuFunctions.add(txtOriginKeywordSearch);
        panelMainMenuFunctions.add(lblOriginKeywordSearch);
        panelMainMenuFunctions.add(txtCargoKeywordSearch);
        panelMainMenuFunctions.add(lblCargoKeywordSearch);
        panelMainMenuFunctions.add(btnShowConcluded);
        panelMainMenuFunctions.add(btnShowCurrent);
        panelMainMenuFunctions.add(btnShowAll);
        panelMainMenuFunctions.add(btnLogOut);
        panelMainMenuFunctions.add(btnSearch);
        panelMainMenuFunctions.add(scrollJourneys);

        panelMainMenuFunctions.add(btnExamine);
        panelMainMenuFunctions.add(btnRegisterJourney);


        journeyRegisterScreen = new JourneyRegisterScreen(parentWindow, this, loggedIn, logisticsCompany);
        ExamineScreen examineScreen;

    }

    public void addJourney(Journey journey) {
        clientJourneys.addRow(new Object[]{journey.getJourneyOrigin().getName(), journey.getJourneyDestination().getName(),
                journey.getContainerContent()});
    }


    private void setEnableButtons(boolean enabled) {
        btnLogOut.setEnabled(enabled);
        btnShowConcluded.setEnabled(enabled);
        btnShowCurrent.setEnabled(enabled);
        btnShowConcluded.setEnabled(enabled);
        btnShowAll.setEnabled(enabled);
        btnRegisterJourney.setEnabled(enabled);


    }

    private void enableButtons() {
        setEnableButtons(true);
    }

    private void disableButtons() {
        setEnableButtons(false);
    }

    public void setVisible(boolean visible) {
        if (!visible) {
            disableButtons();
        } else {
            enableButtons();
        }
        panelMainMenuFunctions.setVisible(visible);
    }

    public void display(List<Journey> searchResults) {
        for (Journey journey : searchResults) {
            clientJourneys.addRow(new Object[]{journey.getJourneyOrigin().getName(), journey.getJourneyDestination().getName(), journey.getContainerContent(), journey.getId()});
            this.repaint();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        initialize();

    }

}

