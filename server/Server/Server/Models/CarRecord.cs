using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.ComponentModel.DataAnnotations.Schema;
using System.Linq;
using System.Threading.Tasks;

namespace Server.Models
{
    public class CarRecord
    {
        public int Id { get; set; }
        public string Vin { get; set; }
        public double Temperature { get; set; }
        public double Speed { get; set; }
        public double Voltage { get; set; }
        public double FuelUsage { get; set; }
        public int EngineSpeed { get; set; }

        //[Required, DatabaseGenerated(DatabaseGeneratedOption.Computed)]
        //public DateTime CreatedAt { get; set; }
    }
}
