 const studentData = [
    { stid: "u123", fname: "Zima", lname: "Patel" },
    { stid: "u234", fname: "Lakshmi", lname: "Angara" },
    { stid: "u345", fname: "Alex", lname: "Infante" },
    { stid: "u456", fname: "Jared", lname: "Peterson" },
    { stid: "u567", fname: "Jane", lname: "Doe" },
    { stid: "u678", fname: "John", lname: "Smith" },
    { stid: "u789", fname: "Carole", lname: "Baskin" },
    { stid: "u891", fname: "Joe", lname: "Exotic" },
];

function validate() {
    var fname_ = document.getElementById("fname").value;
    var lname_ = document.getElementById("lname").value;
    var stid_ = document.getElementById("studentid").value;

    for (i = 0; i < studentData.length; i++) {
        x = studentData[i].stid.toUpperCase() == stid_.toUpperCase();
        y = studentData[i].fname.toUpperCase() == fname_.toUpperCase();
        z = studentData[i].lname.toUpperCase() == lname_.toUpperCase();

        if (x && y && z) {
        localStorage.setItem("id", stid_);
        localStorage.setItem("fn", fname_);
        localStorage.setItem("ln", lname_);
        
        console.log(fname_);
        alert("Signed in as " + fname_);
        document.location.href = "studentprojects.html";
        break;
        }
        
        x=null;
        y=null;
        z=null;
    }

    if (!x && !y && !z) {
        alert("invalid credentials");
        document.getElementById('fname').value = '';
        document.getElementById('lname').value = '';
        document.getElementById('studentid').value = '';
  }
}
