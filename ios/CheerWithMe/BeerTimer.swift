//
//  BeerTimer.swift
//  CheerWithMe
//
//  Created by Johan Lindskogen on 2020-07-24.
//  Copyright © 2020 Johan Lindskogen. All rights reserved.
//

import SwiftUI

struct TimerBlock: View {
    let backgroundColor: UInt32
    let titleText: String
    let subtitleText: String
    
    var body: some View {
        RoundedRectangle(cornerRadius: 5.0)
            .aspectRatio(contentMode: .fit)
            .foregroundColor(.hex(backgroundColor))
            .overlay(
                VStack{
                    Text(titleText).font(.largeTitle).bold()
                    Text(subtitleText).font(.caption).kerning(2.0)
            })
    }
}

func formatInterval(_ time: TimeInterval, component: NSCalendar.Unit) -> String {
    let formatter = DateComponentsFormatter()
    formatter.allowedUnits = [component]
    formatter.unitsStyle = .positional
    formatter.maximumUnitCount = 1
    
    return formatter.string(for: time) ?? "0"
}

struct BeerTimer: View {
    @State var time: TimeInterval = 0
    let timer = Timer.publish(every: 1, on: .main, in: .common).autoconnect()
    
    var body: some View {
        VStack {
            HStack {
                TimerBlock(backgroundColor: 0xa9b6b6, titleText: formatInterval(time, component: .day), subtitleText: "DAYS")
                TimerBlock(backgroundColor: 0xc96e12, titleText: formatInterval(time, component: .hour), subtitleText: "HOURS")
            }.foregroundColor(.white)
            HStack {
                TimerBlock(backgroundColor: 0x262f0d, titleText: formatInterval(time, component: .minute), subtitleText: "MINUTES")
                TimerBlock(backgroundColor: 0xec9d00, titleText: formatInterval(time, component: .second), subtitleText: "SECONDS")
            }.foregroundColor(.white)
            Text("BEER O' CLOCK")
                .font(.headline)
                .kerning(3.0)
                .foregroundColor(.hex(0xC96E12))
        }.padding()
        .onReceive(timer) { nowDate in
            print("hello")
            var dateComponents = DateComponents()
            dateComponents.year = 2020
            dateComponents.month = 7
            dateComponents.day = 24
            dateComponents.hour = 15
            dateComponents.minute = 0
            let calendar = Calendar.current
            if let nextDate = calendar.date(from: dateComponents) {
                // HERE
                time = nowDate.timeIntervalSince(nextDate)
            }
            
        }
    }
}

struct BeerTimer_Previews: PreviewProvider {
    static var previews: some View {
        BeerTimer()
    }
}
