(function () {
  const photos = {
    Football: "images/ug-football.png",
    Basketball: "images/ug-basketball.png",
    Tennis: "images/ug-tennis.png",
    Boxing: "images/ug-boxing.png",
    Swimming: "images/ug-swim.png",
    Athletics: "images/ug-track.png"
  };

  const events = [
    { id: "ev-foot", title: "Uganda vs Senegal", sport: "Football", date: "14 Jun", time: "19:00", city: "Kampala", venue: "Nakivubo Stadium", price: 45000, hours: 54 },
    { id: "ev-bball", title: "Cranes vs Kenya", sport: "Basketball", date: "15 Jun", time: "18:00", city: "Kampala", venue: "Lugogo Arena", price: 25000, hours: 78 },
    { id: "ev-foot2", title: "Kenya vs Tanzania", sport: "Football", date: "16 Jun", time: "16:00", city: "Kampala", venue: "Mandela National Stadium", price: 35000, hours: 96 },
    { id: "ev-tennis", title: "Kampala Open final", sport: "Tennis", date: "16 Jun", time: "15:00", city: "Kampala", venue: "Lugogo Tennis Club", price: 18000, hours: 98 },
    { id: "ev-box", title: "East Africa Fight Night", sport: "Boxing", date: "17 Jun", time: "20:00", city: "Kampala", venue: "MTN Arena", price: 22000, hours: 126 },
    { id: "ev-swim", title: "Nile Swim Cup", sport: "Swimming", date: "18 Jun", time: "10:00", city: "Kampala", venue: "Kampala Aquatic Centre", price: 12000, hours: 140 },
    { id: "ev-track", title: "National Athletics Trials", sport: "Athletics", date: "19 Jun", time: "09:00", city: "Kampala", venue: "Mandela National Stadium", price: 8000, hours: 163 },
    { id: "ev-tennis2", title: "Lakeside invitational", sport: "Tennis", date: "20 Jun", time: "14:00", city: "Entebbe", venue: "Botanical Gardens courts", price: 10000, hours: 192 }
  ];

  const coaches = [
    { name: "Amina Okello", sport: "Football", role: "Head coach", years: 11, bio: "Builds the Cranes' match-week plan and the set-piece sheet for Nakivubo.", img: "images/ug-coach-amina.png", rating: 4.9, wins: 86, draws: 21, losses: 14, licence: "CAF A", clubs: "Vipers SC · Uganda Cranes" },
    { name: "David Ssali", sport: "Basketball", role: "Head coach", years: 9, bio: "Runs the Lugogo offence and the national-team camp before the Kenya window.", img: "images/ug-coach-david.png", rating: 4.7, wins: 112, draws: 0, losses: 38, licence: "FIBA Level 3", clubs: "City Oilers · Uganda" },
    { name: "Grace Namutebi", sport: "Tennis", role: "High-performance coach", years: 8, bio: "Prepares the Kampala Open finalists and the lakeside invitational draw.", img: "images/ug-coach-grace.png", rating: 4.8, wins: 64, draws: 0, losses: 18, licence: "ITF coaching", clubs: "Lugogo Club · Kampala Open" },
    { name: "Joseph Kato", sport: "Boxing", role: "Head coach", years: 14, bio: "Corners the East Africa Fight Night card and the amateur undercard.", img: "images/ug-coach-joseph.png", rating: 4.6, wins: 41, draws: 0, losses: 9, licence: "National corners licence", clubs: "MTN Arena · Uganda Boxing" },
    { name: "Joel Tumusiime", sport: "Swimming", role: "Head coach", years: 7, bio: "Sets the Nile Swim Cup warm-up and the sprint final lineup.", img: "images/ug-coach-ruth.png", rating: 4.8, wins: 22, draws: 0, losses: 6, licence: "World Aquatics", clubs: "Nile Swim Club · Kampala Aquatic" },
    { name: "Mercy Adong", sport: "Athletics", role: "Sprints coach", years: 12, bio: "Calls the 100m trials and the photo-finish relays at Mandela.", img: "images/ug-coach-peter.png", rating: 4.9, wins: 18, draws: 0, losses: 4, licence: "World Athletics Level 2", clubs: "Mandela Track Club · Uganda" }
  ];

  const notices = [
    { title: "Sarah Nakato challenged you", detail: "Athletics · Marathon", when: "2m" },
    { title: "Mandela Track Club session", detail: "Saturday tempo at the track", when: "1h" },
    { title: "Amina Okello", detail: "Booked Tuesday 09:00", when: "3h" },
    { title: "Watch recovery is 82", detail: "Ready for a session", when: "Today" }
  ];

  const bookDays = ["Mon 28", "Tue 29", "Wed 30", "Thu 1", "Fri 2", "Sat 3", "Sun 4"];
  const bookTimes = ["06:30", "09:00", "16:00", "18:30"];

  const ads = [
    { id: "nile-cafe", brand: "Nile Cafe", place: "Nakivubo", line: "Match-day pours and a trail stamp.", detail: "Stamp + match-day menu. Open with the Uganda vs Senegal crowd.", img: "images/ug-cafe.png" },
    { id: "nakasero", brand: "Nakasero craft market", place: "Nakasero", line: "Kampala's oldest market — crafts, coffee, and a trail partner stall.", detail: "Stamp + UTB-certified crafts.", img: "images/ug-market.png" },
    { id: "entebbe", brand: "Entebbe Botanical Gardens", place: "Entebbe", line: "Next stamp on the trail, beside the lakeside invitational courts.", detail: "Trail partner · gardens and the 20 Jun tennis card.", img: "images/ug-gardens.png" },
    { id: "nile-source", brand: "Source of the Nile", place: "Jinja", line: "Where the Nile begins. Partner check-in on the riverfront.", detail: "Trail partner · riverfront. Linked from the travel reel.", img: "images/ug-nile.png" }
  ];

  const merch = [
    { id: "m1", name: "Training jersey", category: "Kit", price: 85000, detail: "Black and deep green match jersey. Breathable, club crest on the chest.", img: "images/ug-merch-jersey.png", sizes: ["S", "M", "L", "XL"] },
    { id: "m2", name: "Coach shell", category: "Coaching", price: 120000, detail: "Sideline jacket for match week. Lightweight, deep green trim.", img: "images/ug-merch-shell.png", sizes: ["S", "M", "L", "XL"] },
    { id: "m3", name: "Court hoodie", category: "Kit", price: 95000, detail: "Warm-up hoodie for indoor sessions and travel days.", img: "images/ug-merch-hoodie.png", sizes: ["S", "M", "L", "XL"] },
    { id: "m4", name: "Match ball", category: "Training", price: 70000, detail: "Match-weight ball for coaching sessions and club finals.", img: "images/ug-merch-ball.png", sizes: ["Size 7"] },
    { id: "m5", name: "Track singlet", category: "Kit", price: 45000, detail: "Race singlet for trials and club meets.", img: "images/ug-merch-singlet.png", sizes: ["XS", "S", "M", "L"] },
    { id: "m6", name: "Session cap", category: "Training", price: 25000, detail: "Sun cap for outdoor coaching blocks.", img: "images/ug-merch-cap.png", sizes: ["One size"] }
  ];

  const sports = ["All", "Football", "Basketball", "Tennis", "Boxing", "Swimming", "Athletics"];
  const palette = ["#0c3d2e", "#0a0a0a", "#145c43", "#1a1a1a", "#1f6b4a", "#12382c", "#2a2a2a"];

  function ugx(amount) {
    return "UGX " + amount.toLocaleString("en-US");
  }
  function countdown(hours) {
    return Math.floor(hours / 24) + "d " + (hours % 24) + "h";
  }
  function initials(name) {
    return name.split(" ").map((part) => part[0]).slice(0, 2).join("").toUpperCase();
  }
  function colorFor(name) {
    let hash = 0;
    for (const ch of name) hash = (hash * 31 + ch.charCodeAt(0)) | 0;
    return palette[Math.abs(hash) % palette.length];
  }
  function roster() {
    const first = ["Farouk","Sarah","Denis","Joan","Emmanuel","Patricia","Brian","Lydia","Mark","Isaac","Sharon","Moses","Claire","Joel","Anita","Joshua","Mercy","Samuel","Hannah","Peter","Amina","Daniel","Ruth","Joseph","Grace","David","Nora","Simon","Esther","Paul"];
    const last = ["Magumba","Achieng","Wasswa","Nankya","Otim","Akello","Ssemakula","Nambi","Byaruhanga","Lubega","Atim","Waiswa","Mbabazi","Tumusiime","Kiconco","Adong","Ocen","Namara","Kato","Nakato","Mugisha","Okello","Ssali","Namutebi","Babirye","Ojok","Auma","Ssebugwawo","Nabirye","Opio"];
    const specs = [
      ["Football", 23, ["Striker","Midfielder","Defender","Goalkeeper","Winger"], ["Vipers SC","KCCA FC","SC Villa","URA FC"], "goals", 174],
      ["Basketball", 15, ["Point guard","Shooting guard","Small forward","Power forward","Center"], ["City Oilers","Namuwongo Blazers","KCCA Leopards"], "points", 182],
      ["Tennis", 12, ["Singles","Doubles"], ["Lugogo Club","Kampala Club","Entebbe Club"], "titles", 170],
      ["Boxing", 10, ["Welterweight","Featherweight","Light heavyweight","Flyweight","Middleweight"], ["MTN Arena","Nakivubo gym","Lugogo gym"], "wins", 168],
      ["Swimming", 18, ["Freestyle","Butterfly","Backstroke","Breaststroke"], ["Kampala Aquatic Centre","Nile Swim Club"], "medals", 172],
      ["Athletics", 28, ["100m","1500m","Long jump","400m","Marathon","High jump"], ["Mandela Track Club","Kampala Distance","Jinja Athletics"], "medals", 168]
    ];
    const cities = ["Kampala","Entebbe","Jinja","Gulu"];
    const used = new Set();
    const out = [];
    specs.forEach((spec, sportIndex) => {
      const [sport, count, positions, clubs, scoreLabel, heightBase] = spec;
      for (let i = 0; i < count; i++) {
        let shift = 0;
        let name = "";
        do {
          const fi = (i * 3 + sportIndex * 5 + shift) % first.length;
          const li = (i * 2 + sportIndex * 7 + shift) % last.length;
          name = first[fi] + " " + last[li];
          shift += 1;
        } while (used.has(name) && shift < 40);
        used.add(name);
        const appearances = 6 + (i * 2) % 22;
        const scoring = (i * 3 + sportIndex) % 18;
        const city = cities[(i + sportIndex) % cities.length];
        const position = positions[i % positions.length];
        const club = clubs[i % clubs.length];
        out.push({
          name, sport, position, club, city,
          nationality: "Uganda",
          age: 18 + (i * 3 + sportIndex) % 15,
          number: 1 + (i * 7 + sportIndex) % 99,
          heightCm: heightBase + (i * 2) % 18,
          appearances, scoring, scoreLabel,
          bio: name + " is a " + position + " for " + club + ". " + appearances + " appearances this season, based in " + city + "."
        });
      }
    });
    return out;
  }

  function eventPhoto(item) {
    if (item.id === "ev-foot") return "images/ug-hero-match.png";
    if (item.id === "ev-foot2") return "images/ug-stadium.png";
    if (item.id === "ev-tennis2") return "images/ug-gardens.png";
    return photos[item.sport];
  }
  function page(type, query) {
    const params = new URLSearchParams({ type });
    Object.entries(query).forEach(([key, value]) => params.set(key, value));
    return "detail.html?" + params.toString();
  }
  function byRank(list) {
    return list.slice().sort((a, b) => b.scoring - a.scoring || b.appearances - a.appearances || a.name.localeCompare(b.name));
  }
  function loadJson(key, fallback) {
    try { return JSON.parse(localStorage.getItem(key) || "") ?? fallback; } catch (error) { return fallback; }
  }
  function hash(value) {
    let total = 0;
    for (const ch of String(value)) total = (total * 33 + ch.charCodeAt(0)) | 0;
    return Math.abs(total);
  }
  function activitiesFor(person) {
    const titles = {
      Football: ["Match at Nakivubo", "Pitch session", "Recovery run"],
      Basketball: ["Lugogo scrimmage", "Shooting session", "Conditioning"],
      Tennis: ["Practice sets", "Match play", "Footwork block"],
      Boxing: ["Sparring", "Bag work", "Roadwork"],
      Swimming: ["Aerobic set", "Sprint set", "Open-water"],
      Athletics: ["Track session", "Tempo", "Long run"]
    };
    const names = titles[person.sport] || ["Session", "Training", "Recovery"];
    return names.map((title, index) => {
      const seed = hash(person.name + ":" + index);
      const minutes = 24 + (seed % 70);
      let stats;
      if (person.sport === "Basketball") {
        stats = [[(12 + seed % 22) + " pts", "points"], [minutes + " min", "minutes"], [(seed % 11) + " reb", "rebounds"]];
      } else if (person.sport === "Tennis") {
        stats = [[(1 + seed % 3) + " sets", "sets"], [minutes + " min", "time"], [(seed % 14) + " aces", "aces"]];
      } else if (person.sport === "Boxing") {
        stats = [[(3 + index) + " rounds", "rounds"], [minutes + " min", "time"], [(20 + seed % 40), "landed"]];
      } else if (person.sport === "Swimming") {
        const metres = 800 + (seed % 2200);
        stats = [[(metres / 1000).toFixed(1) + " km", "distance"], [minutes + " min", "time"], [Math.round(minutes / (metres / 100) * 10) / 10 + " /100m", "pace"]];
      } else {
        const km = (4 + (seed % 14) + index).toFixed(1);
        const pace = Math.floor(minutes / km) + ":" + String(Math.round((minutes / km % 1) * 60)).padStart(2, "0");
        stats = [[km + " km", "distance"], [minutes + " min", "moving time"], [pace + " /km", "pace"], [(20 + seed % 120) + " m", "elevation"]];
      }
      const kind = index === names.length - 1 ? "Competition" : "Training";
      const distanceKm = person.sport === "Swimming"
        ? Math.round((0.8 + (seed % 22) / 10) * 10) / 10
        : Math.round((4 + (seed % 14) + index) * 10) / 10;
      const paceSeconds = Math.floor(minutes * 60 / distanceKm);
      const pace = Math.floor(paceSeconds / 60) + ":" + String(paceSeconds % 60).padStart(2, "0") + " /km";
      const avgHr = 126 + (seed % 32);
      const maxHr = 162 + (seed % 22);
      const elevationM = seed % 160;
      const effort = 5 + (seed % 5);
      const splits = [0, 1, 2, 3, 4].map((split) => {
        const seconds = Math.max(60, paceSeconds + (split - 2) * 6);
        return Math.floor(seconds / 60) + ":" + String(seconds % 60).padStart(2, "0");
      });
      return {
        index, title, when: ["Mon 22", "Wed 24", "Fri 26"][index], stats, kudos: 6 + (seed % 48),
        kind, date: ["Mon 22", "Wed 24", "Fri 26"][index], distanceKm, minutes, avgHr, maxHr, elevationM, effort, pace, splits,
        notes: person.name + " logged this " + person.sport + " block in " + (person.city || "Kampala") + ". Effort stayed steady through the main set."
      };
    });
  }
  function personalTraining() {
    return [
      { index: 0, title: "Dawn run", kind: "Training", date: "Mon 22", when: "Mon 22", distanceKm: 8.4, minutes: 46, avgHr: 148, maxHr: 171, elevationM: 42, effort: 6, pace: "5:28 /km", notes: "Easy start out of Kololo, then a steady climb toward the ridge.", kudos: 12, stats: [["8.4 km", "distance"], ["46 min", "moving time"], ["5:28 /km", "pace"], ["42 m", "elevation"]] },
      { index: 1, title: "Pitch session", kind: "Training", date: "Wed 24", when: "Wed 24", distanceKm: 6.1, minutes: 72, avgHr: 139, maxHr: 166, elevationM: 12, effort: 7, pace: "11:48 /km", notes: "Passing patterns, finishing, and a 20-minute conditioned game.", kudos: 9, stats: [["6.1 km", "distance"], ["72 min", "moving time"], ["11:48 /km", "pace"], ["12 m", "elevation"]] },
      { index: 2, title: "Recovery jog", kind: "Training", date: "Fri 26", when: "Fri 26", distanceKm: 5.2, minutes: 34, avgHr: 122, maxHr: 141, elevationM: 18, effort: 4, pace: "6:32 /km", notes: "Short and quiet. Heart rate stayed in the easy band.", kudos: 4, stats: [["5.2 km", "distance"], ["34 min", "moving time"], ["6:32 /km", "pace"], ["18 m", "elevation"]] },
      { index: 3, title: "Club match", kind: "Competition", date: "Sat 27", when: "Sat 27", distanceKm: 10.6, minutes: 95, avgHr: 156, maxHr: 182, elevationM: 8, effort: 9, pace: "8:57 /km", notes: "Full match at the club ground. Second half pace held.", kudos: 31, stats: [["10.6 km", "distance"], ["95 min", "moving time"], ["8:57 /km", "pace"], ["8 m", "elevation"]] }
    ].map((item) => {
      const paceSeconds = Math.floor(item.minutes * 60 / item.distanceKm);
      item.splits = [0, 1, 2, 3, 4].map((split) => {
        const seconds = Math.max(60, paceSeconds + (split - 2) * 6);
        return Math.floor(seconds / 60) + ":" + String(seconds % 60).padStart(2, "0");
      });
      return item;
    });
  }
  function loggedActivity(item, index) {
    const distanceKm = Number(String(item.distance || "").replace(/[^\d.]/g, "")) || 0;
    const minutes = Number(item.minutes) || 0;
    const paceSeconds = distanceKm ? Math.floor(minutes * 60 / distanceKm) : 0;
    const pace = paceSeconds ? Math.floor(paceSeconds / 60) + ":" + String(paceSeconds % 60).padStart(2, "0") + " /km" : "—";
    const seed = hash((item.title || "session") + ":" + index);
    const avgHr = 128 + (seed % 30);
    const maxHr = 160 + (seed % 24);
    const elevationM = seed % 80;
    const effort = 4 + (seed % 6);
    const count = distanceKm ? Math.min(6, Math.max(1, Math.ceil(distanceKm))) : 0;
    const splits = Array.from({ length: count }, (_, split) => {
      const seconds = Math.max(60, (paceSeconds || 300) + (split - 2) * 6);
      return Math.floor(seconds / 60) + ":" + String(seconds % 60).padStart(2, "0");
    });
    return {
      index, logged: true, title: item.title || "Session", when: item.at || "Just now",
      kind: "Training", sport: item.sport || "Football", distanceKm, minutes,
      avgHr, maxHr, elevationM, effort, pace, splits, kudos: item.kudos || 0,
      notes: "You logged " + (item.title || "this session") + " as " + (item.sport || "training") + ".",
      stats: [
        [distanceKm ? distanceKm.toFixed(1) + " km" : (item.distance || "—"), "distance"],
        [minutes + " min", "moving time"],
        [pace, "pace"],
        [elevationM + " m", "elevation"]
      ]
    };
  }
  function reportOf(list) {
    const minutes = list.reduce((sum, item) => sum + (item.minutes || 0), 0);
    const km = list.reduce((sum, item) => sum + (item.distanceKm || 0), 0);
    return {
      km: km.toFixed(1),
      time: Math.floor(minutes / 60) + "h " + (minutes % 60) + "m",
      sessions: list.filter((item) => item.kind !== "Competition").length,
      comps: list.filter((item) => item.kind === "Competition").length
    };
  }
  function clubsFor(players) {
    const groups = new Map();
    players.forEach((player) => {
      if (!groups.has(player.club)) groups.set(player.club, []);
      groups.get(player.club).push(player);
    });
    return [...groups.entries()].map(([name, members]) => {
      const lead = members.slice().sort((a, b) => b.scoring - a.scoring || a.name.localeCompare(b.name))[0];
      return { name, sport: lead.sport, city: lead.city, members: members.length, lead, img: photos[lead.sport] };
    }).sort((a, b) => b.members - a.members).slice(0, 8);
  }
  function featuredAthletes(players) {
    return ["Football", "Basketball", "Tennis", "Boxing", "Swimming", "Athletics"].map((sport) => {
      return players.filter((player) => player.sport === sport).sort((a, b) => b.scoring - a.scoring || a.name.localeCompare(b.name))[0];
    }).filter(Boolean);
  }
  function coachRecord(coach) {
    return coach.draws ? coach.wins + "–" + coach.draws + "–" + coach.losses : coach.wins + "–" + coach.losses;
  }
  function coachWinRate(coach) {
    const played = coach.wins + coach.draws + coach.losses;
    return played ? Math.round(coach.wins * 100 / played) : 0;
  }
  function slotOpen(coach, day, time) {
    return (day * 3 + time + coach.wins) % 4 !== 0;
  }
  function loadCreatedEvents() {
    const list = loadJson("timwork_events", []);
    return Array.isArray(list) ? list : [];
  }
  function allEvents() {
    return loadCreatedEvents().concat(events);
  }
  function createEvent(item) {
    const list = loadCreatedEvents();
    list.unshift(item);
    localStorage.setItem("timwork_events", JSON.stringify(list));
  }
  function weeks(name) {
    return Array.from({ length: 12 }, (_, index) => 18 + (hash(name + index) % 82));
  }
  function loadSocial() {
    const data = loadJson("timwork_social", {});
    return {
      follows: data.follows || [],
      posts: data.posts && !Array.isArray(data.posts) ? data.posts : {},
      likes: data.likes || {},
      comments: data.comments || {},
      sessions: Array.isArray(data.sessions) ? data.sessions : [],
      challenges: data.challenges || [],
      bookings: data.bookings || {},
      orders: data.orders || []
    };
  }
  function saveSocial(data) {
    localStorage.setItem("timwork_social", JSON.stringify(data));
  }
  function loadBag() {
    const bag = loadJson("timwork_bag", []);
    return Array.isArray(bag) ? bag : [];
  }
  function saveBag(bag) {
    localStorage.setItem("timwork_bag", JSON.stringify(bag));
  }
  function session() {
    return loadJson("timwork_session", null);
  }

  window.Timwork = {
    photos, events, coaches, ads, merch, sports, palette,
    ugx, countdown, initials, colorFor, roster, eventPhoto, page, byRank, hash, activitiesFor, weeks,
    personalTraining, loggedActivity, reportOf, clubsFor, featuredAthletes, coachRecord, coachWinRate, slotOpen,
    allEvents, createEvent, notices, bookDays, bookTimes,
    loadSocial, saveSocial, loadBag, saveBag, session
  };
})();
