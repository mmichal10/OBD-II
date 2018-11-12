using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Threading.Tasks;
using Microsoft.AspNetCore.Mvc;
using Server.Models;
using Server.Data;

namespace Server.Controllers
{
    public class HomeController : Controller
    {
        private readonly ApplicationDbContext _context;
        public HomeController(ApplicationDbContext applicationDbContext)
        {
            _context = applicationDbContext;
        }
        public IActionResult Index()
        {
            if (_context.CarRecords.Count() > 0) { 
            @ViewBag.vins = _context.CarRecords.Select(x => x.Vin).ToList();
            }
            else
            {
                @ViewBag.vins = new List<string>();
            }

            return View(_context.CarRecords.ToList());
        }
        [HttpGet]
        public IActionResult Index2()
        {
            @ViewBag.vins = _context.CarRecords.Select(x => x.Vin).ToList();
            return View(new List<CarRecord>());
        }

        public IActionResult Contact()
        {
            ViewData["Message"] = "Your contact page.";

            return View();
        }

        public IActionResult Error()
        {
            return View(new ErrorViewModel { RequestId = Activity.Current?.Id ?? HttpContext.TraceIdentifier });
        }
        [HttpPost]
        public IActionResult Archive([FromBody]CarRecord carRecord)
        {
            _context.CarRecords.Add(carRecord);
        
            _context.SaveChanges();
            return Json(carRecord);
        }
            
        [HttpGet("{vin}")]
        public IActionResult Index(string vin)
        {
            var vinRecords = _context.CarRecords.Where(x => x.Vin==vin);
            ViewBag.vinRecords = vinRecords;
            return View(vinRecords); 
        }

    }
}
