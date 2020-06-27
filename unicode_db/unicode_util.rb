#!/bin/ruby

DB = File.open("UnicodeData.txt")

def parse_codepoint(str)
    ucs2 = str.to_i(16)
    return [ucs2].pack('U')
end

def each_entry
    DB.each_line do |l|
        ary = l.split(';')
        sym = parse_codepoint(ary[0])
        desc = ary[1].downcase.gsub(' ', '-')
        yield sym, desc
    end
end

DBHASH = {}

each_entry do |sym, desc|
    #~ print "#{sym}/:#{desc}:\n"
    DBHASH[sym] = desc
end

def add_shortcodes(src, dst)
    src_file = File.open(src, 'r')
    dst_file = File.open(dst, 'w')

    src_file.each_line do |sl|
        sym = sl.chomp
        desc = DBHASH[sym]
        if desc.nil?
            puts "Entry for #{sym} not found in DB"
            next
        end

        dst_file.puts "#{sym}/#{desc}"
    end

    src_file.close
    dst_file.close
end
